package algolia.ingestor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.nifi.documentation.init.NopComponentLog;
import org.apache.nifi.logging.ComponentLog;
import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchConfig;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.BatchIndexingResponse;
import com.algolia.search.models.indexing.BrowseIndexQuery;
import com.algolia.search.models.rules.FacetOrdering;
import com.algolia.search.models.rules.FacetValuesOrder;
import com.algolia.search.models.rules.FacetsOrder;
import com.algolia.search.models.rules.RenderingContent;
import com.algolia.search.models.rules.Rule;
import com.algolia.search.models.settings.IndexSettings;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import processors.ingestor.constants.LoadMode;
import processors.ingestor.constants.PropagationStartEnvironment;
import processors.ingestor.model.Record;

public class AlgoliaUtil {
    // private static final Logger LOGGER =
    // Logger.getLogger(AlgoliaUtil.class.getName());
    private SearchClient client;
    private ComponentLog logger = new NopComponentLog();

    private IndexNamesConf indexesNamesConf;

    public AlgoliaUtil(String applicationID, String apiKey, String indexes_names_conf,
            Integer batchSize) throws IOException {
        client = DefaultSearchClient.create(new SearchConfig.Builder(applicationID, apiKey)
                .setBatchSize(batchSize != null ? batchSize.intValue() : 1000).build());
        indexesNamesConf = readValue(IndexNamesConf.class, indexes_names_conf);
    }

    /**
     * @return SearchClient return the client
     */
    public SearchClient getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(SearchClient client) {
        this.client = client;
    }

    /**
     * @return ComponentLog return the logger
     */
    public ComponentLog getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(ComponentLog logger) {
        this.logger = logger;
    }

    /**
     * @return IndexNamesConf return the indexesNamesConf
     */
    public IndexNamesConf getIndexesNamesConf() {
        return indexesNamesConf;
    }

    /**
     * @param indexesNamesConf the indexesNamesConf to set
     */
    public void setIndexesNamesConf(IndexNamesConf indexesNamesConf) {
        this.indexesNamesConf = indexesNamesConf;
    }

    public String generateIndexName(String environment, String storeIdentifier, String locale) {
        final String BASE_INDEX_NAME_FORMAT = "%s_%s_%s";
        return String.format(BASE_INDEX_NAME_FORMAT, environment, storeIdentifier, locale);
    }

    public void performOperation(ArrayList<Record> records, String loadMode, String baseIndexName,
            String target_env) {

        if (target_env.equals(PropagationStartEnvironment.auth.toString())) {
            baseIndexName = generateBaseAuthIndexName(baseIndexName);
        } else {
            baseIndexName = generateBaseLiveIndexName(baseIndexName);
        }
        String ungroupedIndexName = baseIndexName + indexesNamesConf.getUngroupedIndex().getToken();
        String groupedIndexName = baseIndexName + indexesNamesConf.getGroupedIndex().getToken();
        SearchIndex<Record> ungrouped_index = client.initIndex(ungroupedIndexName, Record.class);
        SearchIndex<Record> grouped_index = client.initIndex(groupedIndexName, Record.class);

        if (loadMode.equals(LoadMode.save.toString())) {

            logger.info(String.format("Read new records id"));
            Set<String> newObjectSet = records.stream().map(Record::getObjectID).collect(Collectors.toSet());
            logger.info(String.format("Read old records id"));
            Set<String> oldObjectSet = StreamSupport
                    .stream(ungrouped_index.browseObjects(new BrowseIndexQuery())
                            .spliterator(), false)
                    .map(Record::getObjectID).collect(Collectors.toSet());

            logger.info(String.format("Compute records to remove"));
            oldObjectSet.removeAll(newObjectSet);
            List<String> record_to_delete = oldObjectSet.stream().collect(Collectors.toList());
            if (record_to_delete.size() > 0) {
                logger.info(String.format("Records to delete %d for index %s",
                        record_to_delete.size(), ungroupedIndexName));
                logger.info(String.format("Start delete records from %s", ungroupedIndexName));
                BatchIndexingResponse ungroupedIndexDelRes = ungrouped_index.deleteObjects(record_to_delete);
                logger.info(String.format("Wait for delete task for %s", ungroupedIndexName));
                ungroupedIndexDelRes.waitTask();
                logger.info(String.format("End delete records from %s", ungroupedIndexName));
                logger.info(String.format("Start delete records from %s", groupedIndexName));
                BatchIndexingResponse groupedIndexDelRes = grouped_index.deleteObjects(record_to_delete);
                logger.info(String.format("Wait for delete task for %s", groupedIndexName));
                groupedIndexDelRes.waitTask();
                logger.info(String.format("End delete records from %s", groupedIndexName));
            } else {
                logger.info(String.format("No records to remove"));
            }
            logger.info(String.format("Start saveObjects in %s", ungroupedIndexName));
            BatchIndexingResponse ungroupedIndexSaveRes = ungrouped_index.saveObjects(records);
            logger.info(String.format("Wait for saveObjects task for %s", ungroupedIndexName));
            ungroupedIndexSaveRes.waitTask();
            logger.info(String.format("End saveObjects in %s", ungroupedIndexName));
            logger.info(String.format("Start saveObjects in %s", groupedIndexName));
            BatchIndexingResponse groupedIndexSaveRes = grouped_index.saveObjects(records);
            logger.info(String.format("Wait for saveObjects task for %s", groupedIndexName));
            groupedIndexSaveRes.waitTask();
            logger.info(String.format("End saveObjects in %s", groupedIndexName));

            // TODO: we might try to use "replaceAllObjects" as an alternative Algolia
            // method to
            // Browse+Delete+Save

        } else if (loadMode.equals(LoadMode.update.toString())) {
            ungrouped_index.partialUpdateObjects(records).waitTask();
            grouped_index.partialUpdateObjects(records).waitTask();
        }
    }

    public void setIndexSettings(String baseIndexName, String index_settings_conf,
            String conditionless_rule_conf, String renderingContent_conf, String lang_conf)
            throws IOException {

        // CREATE PRIMARY INDEXES
        createPrimaryIndex(baseIndexName, indexesNamesConf.getUngroupedIndex().getToken(),
                indexesNamesConf.getUngroupedIndex().getAttributeForDistinct(),
                indexesNamesConf.getVirtualReplicas());
        createPrimaryIndex(baseIndexName, indexesNamesConf.getGroupedIndex().getToken(),
                indexesNamesConf.getGroupedIndex().getAttributeForDistinct(),
                indexesNamesConf.getVirtualReplicas());

        // READ LANGUAGE CONFIGURATION
        LangConf language_conf = readValue(LangConf.class, lang_conf);

        // SET INDEX SETTINGS
        IndexSettingsConf indexes_settings_conf = readValue(IndexSettingsConf.class, index_settings_conf);
        IndexSettingsConf mergedSettings = mergeIndexSettings(indexes_settings_conf,
                language_conf.get(getLocale(baseIndexName)).getIndexSettings());

        setPrimaryIndexSettings(baseIndexName, indexesNamesConf.getUngroupedIndex().getToken(),
                mergedSettings.getPrimaryIndexes(), mergedSettings.getIndexesAndReplicas());
        setPrimaryIndexSettings(baseIndexName, indexesNamesConf.getGroupedIndex().getToken(),
                mergedSettings.getPrimaryIndexes(), mergedSettings.getIndexesAndReplicas());

        // SET RENDERING CONTENT
        RenderingContent rendering_content_conf = readValue(RenderingContent.class, renderingContent_conf);
        RenderingContent mergedContent = mergeRenderingContent(rendering_content_conf,
                language_conf.get(getLocale(baseIndexName)).getRenderingContent());

        setRenderingContent(baseIndexName, indexesNamesConf.getGroupedIndex().getToken(),
                mergedContent);

        // SET REPLICAS CUSTOM RANKING
        for (int i = 0; i < indexesNamesConf.getVirtualReplicas().size(); i++) {
            setReplicaSettings(baseIndexName, indexesNamesConf.getUngroupedIndex().getToken(),
                    indexesNamesConf.getVirtualReplicas().get(i));

            setReplicaSettings(baseIndexName, indexesNamesConf.getGroupedIndex().getToken(),
                    indexesNamesConf.getVirtualReplicas().get(i));
        }

        Rule rule = readValue(Rule.class, conditionless_rule_conf);

        setConditionlessRule(baseIndexName + indexesNamesConf.getUngroupedIndex().getToken(), rule);
        setConditionlessRule(baseIndexName + indexesNamesConf.getGroupedIndex().getToken(), rule);
    }

    public void setRenderingContent(String baseIndexName, String token,
            RenderingContent renderingContent) {
        SearchIndex<Record> index = client.initIndex(baseIndexName + token, Record.class);
        index.setSettings(new IndexSettings().setRenderingContent(renderingContent)).waitTask();
    }

    public void setReplicaSettings(String baseIndexName, String indexToken,
            VirtualReplicaConf virtualReplica) {
        String indexName = baseIndexName + indexToken + virtualReplica.getToken();

        SearchIndex<Record> index = client.initIndex(indexName, Record.class);
        index.setSettings(new IndexSettings().setCustomRanking(virtualReplica.getCustomRanking()))
                .waitTask();
    }

    public void createPrimaryIndex(String baseIndexName, String indexToken, String distinctField,
            ArrayList<VirtualReplicaConf> replicas) {
        String indexName = baseIndexName + indexToken;
        SearchIndex<Record> index = client.initIndex(indexName, Record.class);
        index.setSettings(new IndexSettings().setAttributeForDistinct(distinctField)
                .setReplicas(generateReplicaNames(indexName, replicas))).waitTask();
    }

    public void setPrimaryIndexSettings(String baseIndexName, String indexToken,
            IndexSettings primaryIndexSettings, IndexSettings primaryAndReplicasSettings) {
        String indexName = baseIndexName + indexToken;
        SearchIndex<Record> index = client.initIndex(indexName, Record.class);

        index.setSettings(primaryIndexSettings).waitTask();

        boolean forwardToReplicas = true;
        index.setSettings(primaryAndReplicasSettings, forwardToReplicas).waitTask();
    }

    public void propagationIndex(String baseIndexName, String start_env) {

        String auth_base_index_name = start_env.equals(PropagationStartEnvironment.live.toString())
                ? generateBaseAuthIndexName(baseIndexName)
                : baseIndexName;
        String live_base_index_name = start_env.equals(PropagationStartEnvironment.auth.toString())
                ? generateBaseLiveIndexName(baseIndexName)
                : baseIndexName;

        // PRIMARY STAGING INDEXES
        SearchIndex<Record> staging_ungrouped_index = client.initIndex(
                auth_base_index_name + indexesNamesConf.getUngroupedIndex().getToken(),
                Record.class);

        SearchIndex<Record> staging_grouped_index = client.initIndex(
                auth_base_index_name + indexesNamesConf.getGroupedIndex().getToken(), Record.class);

        // PRIMARY LIVE INDEXES
        SearchIndex<Record> live_ungrouped_index = client.initIndex(
                live_base_index_name + indexesNamesConf.getUngroupedIndex().getToken(),
                Record.class);
        SearchIndex<Record> live_grouped_index = client.initIndex(
                live_base_index_name + indexesNamesConf.getGroupedIndex().getToken(), Record.class);

        // TEMPORARY LIVE INDEXES
        SearchIndex<Record> live_tmp_index_ungrouped = client.initIndex(
                generateTemporaryIndexName(
                        live_base_index_name + indexesNamesConf.getUngroupedIndex().getToken()),
                Record.class);
        SearchIndex<Record> live_tmp_index_grouped = client.initIndex(
                generateTemporaryIndexName(
                        live_base_index_name + indexesNamesConf.getGroupedIndex().getToken()),
                Record.class);

        // COPY INDEX FROM STAGING TO TMP
        client.copyIndex(staging_ungrouped_index.getUrlEncodedIndexName(),
                live_tmp_index_ungrouped.getUrlEncodedIndexName()).waitTask();
        client.copyIndex(staging_grouped_index.getUrlEncodedIndexName(),
                live_tmp_index_grouped.getUrlEncodedIndexName()).waitTask();

        // COPY INDEX FROM LIVE TO TMP
        client.copyIndex(live_ungrouped_index.getUrlEncodedIndexName(),
                live_tmp_index_ungrouped.getUrlEncodedIndexName(),
                Arrays.asList("settings", "synonyms", "rules")).waitTask();
        client.copyIndex(live_grouped_index.getUrlEncodedIndexName(),
                live_tmp_index_grouped.getUrlEncodedIndexName(),
                Arrays.asList("settings", "synonyms", "rules")).waitTask();

        // MOVE INDEX FROM TMP TO LIVE
        client.moveIndex(live_tmp_index_ungrouped.getUrlEncodedIndexName(),
                live_ungrouped_index.getUrlEncodedIndexName());
        client.moveIndex(live_tmp_index_grouped.getUrlEncodedIndexName(),
                live_grouped_index.getUrlEncodedIndexName());

    }

    public void setConditionlessRules(String baseIndexName, String conditionless_rule_conf)
            throws IOException {

    }

    public List<String> generateReplicaNames(String indexName,
            ArrayList<VirtualReplicaConf> replicas) {
        ArrayList<String> replicas_names = new ArrayList<String>();
        for (int i = 0; i < replicas.size(); i++) {
            replicas_names.add(generateVirtualString(indexName + replicas.get(i).getToken()));
        }

        return replicas_names;
    }

    public String getLocale(String baseIndexName) {
        return baseIndexName.split("_")[3].split("-")[0];
    }

    public String getBaseIndexName(String indexName) {
        return indexName.split("__")[0];
    }

    public String generateBaseLiveIndexName(String baseIndexName) {
        return baseIndexName.replaceAll(PropagationStartEnvironment.auth.toString(),
                PropagationStartEnvironment.live.toString());
    }

    public String generateBaseAuthIndexName(String baseIndexName) {
        return baseIndexName.replaceAll(PropagationStartEnvironment.live.toString(),
                PropagationStartEnvironment.auth.toString());
    }

    public String generateTemporaryIndexName(String indexName) {
        return indexName + "_tmp";
    }

    public String generateVirtualString(String replicaName) {
        return "virtual(" + replicaName + ")";
    }

    public void setConditionlessRule(String indexName, Rule rule) {
        boolean forwardToReplicas = true;
        SearchIndex<Record> index = client.initIndex(indexName, Record.class);
        index.saveRule(rule, forwardToReplicas);
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static List<String> getLocales(String inputString) {
        List<String> locales = new ArrayList<String>();
        if (!inputString.contains("|")) {
            locales.add(inputString);
        } else {
            locales.addAll(Arrays.asList(inputString.split("\\|")));
        }
        return locales;
    }

    public static <T> T readValue(Class<T> type, String value) throws IOException {
        // String content = readFile(path, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        return objectMapper.readValue(value, type);
    }

    public static RenderingContent mergeRenderingContent(RenderingContent content1,
            RenderingContent content2) {

        List<String> order = new ArrayList<String>();
        Map<String, FacetValuesOrder> values = new LinkedHashMap<String, FacetValuesOrder>();

        if (content1 != null && content1.getFacetOrdering() != null) {
            if (content1.getFacetOrdering().getFacets() != null) {
                order.addAll(content1.getFacetOrdering().getFacets().getOrder());
            }
            if (content1.getFacetOrdering().getValues() != null) {
                values.putAll(content1.getFacetOrdering().getValues());
            }
        }

        if (content2 != null && content2.getFacetOrdering() != null) {
            if (content2.getFacetOrdering().getFacets() != null) {
                order.addAll(content2.getFacetOrdering().getFacets().getOrder());
            }
            if (content2.getFacetOrdering().getValues() != null) {
                values.putAll(content2.getFacetOrdering().getValues());
            }
        }

        return new RenderingContent(new FacetOrdering(new FacetsOrder(order), values));
    }

    public static IndexSettingsConf mergeIndexSettings(IndexSettingsConf settings1,
            IndexSettingsConf settings2) {
        settings1.getPrimaryIndexes()
                .setIndexLanguages(settings2.getPrimaryIndexes().getIndexLanguages());
        settings1.getIndexesAndReplicas()
                .setQueryLanguages(settings2.getIndexesAndReplicas().getQueryLanguages())
                .setRemoveStopWords(settings2.getIndexesAndReplicas().getRemoveStopWords())
                .setIgnorePlurals(settings2.getIndexesAndReplicas().getIgnorePlurals());

        return settings1;
    }

    public static ArrayList<Record> deserializeRecord(String jsonPath) throws JsonParseException,
            UnsupportedEncodingException, FileNotFoundException, IOException {
        String content = readFile(jsonPath, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, Record.class);
        ArrayList<Record> record = objectMapper.readValue(content, listType);
        return record;
    }

    public List<String> getMultipleLocales(String inputString) {
        List<String> locales = new ArrayList<String>();
        if (!inputString.contains("|")) {
            locales.add(inputString);
        } else {
            locales.addAll(Arrays.asList(inputString.split("\\|")));
        }
        return locales;
    }
}
