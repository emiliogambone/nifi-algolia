/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package algolia.ingestor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.logging.LogLevel;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import processors.ingestor.constants.AlgoliaAction;
import processors.ingestor.constants.LoadMode;
import processors.ingestor.constants.NiFiUpdateAttribute;
import processors.ingestor.constants.PropagationStartEnvironment;
import processors.ingestor.model.Record;

@Tags({"algolia ingestor"})
@CapabilityDescription("Interact with Algolia indexes")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute = "", description = "")})
@WritesAttributes({@WritesAttribute(attribute = "", description = "")})
public class AlgoliaIndexManager extends AbstractProcessor {

    public static final Relationship SUCCESS_RELATIONSHIP =
            new Relationship.Builder().name("success").description("Success relationship").build();

    public static final Relationship FAILURE_RELATIONSHIP =
            new Relationship.Builder().name("failure").description("Failure relationship").build();

    private static final LogLevel LogLevel = null;

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    private ComponentLog logger;

    public final PropertyDescriptor actions =
            new PropertyDescriptor.Builder().name("algolia-action").displayName("Algolia Action")
                    .required(true).allowableValues(AlgoliaAction.values())
                    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor conditionless_rule = new PropertyDescriptor.Builder()
            .name("conditionless_rule").displayName("conditionless_rule").required(false)
            .dependsOn(actions, new AllowableValue(AlgoliaAction.setIndexSettings.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor grouped_renderingContent =
            new PropertyDescriptor.Builder().name("grouped_renderingContent")
                    .displayName("grouped_renderingContent").required(false)
                    .dependsOn(actions, new AllowableValue(AlgoliaAction.setIndexSettings.name()))
                    .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor index_replicas_settings = new PropertyDescriptor.Builder()
            .name("index_replicas_settings").displayName("index_replicas_settings").required(false)
            .dependsOn(actions, new AllowableValue(AlgoliaAction.setIndexSettings.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor language_config = new PropertyDescriptor.Builder()
            .name("language_config").displayName("language_config").required(false)
            .dependsOn(actions, new AllowableValue(AlgoliaAction.setIndexSettings.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor load_mode = new PropertyDescriptor.Builder().name("load_mode")
            .displayName("Load mode").required(true).allowableValues(LoadMode.values())
            .dependsOn(actions, new AllowableValue(AlgoliaAction.indexingObjects.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor propagation_start_environment = new PropertyDescriptor.Builder()
            .name("propagation_start_environment").displayName("Propagation start environment")
            .required(true).allowableValues(PropagationStartEnvironment.values())
            .dependsOn(actions, new AllowableValue(AlgoliaAction.indexPropagation.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor index_data_target_environment = new PropertyDescriptor.Builder()
            .name("index_data_target_environment").displayName("Indexing target environment")
            .required(true).allowableValues(PropagationStartEnvironment.values())
            .dependsOn(actions, new AllowableValue(AlgoliaAction.indexingObjects.name()))
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

    public final PropertyDescriptor indexes_and_replicas = new PropertyDescriptor.Builder()
            .name("indexes_and_replicas").displayName("indexes_and_replicas").required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();


    public final PropertyDescriptor batchSize = new PropertyDescriptor.Builder().name("batchSize")
            .displayName("Batch Size").required(false).defaultValue("4000")
            .dependsOn(actions, new AllowableValue(AlgoliaAction.indexingObjects.name()))
            .addValidator(StandardValidators.INTEGER_VALIDATOR).build();

    @Override
    protected void init(final ProcessorInitializationContext context) {

        List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(actions);
        properties.add(conditionless_rule);
        properties.add(grouped_renderingContent);
        properties.add(index_replicas_settings);
        properties.add(indexes_and_replicas);
        properties.add(language_config);
        properties.add(load_mode);
        properties.add(propagation_start_environment);
        properties.add(index_data_target_environment);
        properties.add(batchSize);


        descriptors = new ArrayList<>();
        descriptors = Collections.unmodifiableList(properties);

        relationships = new HashSet<>();
        relationships.add(SUCCESS_RELATIONSHIP);
        relationships.add(FAILURE_RELATIONSHIP);
        relationships = Collections.unmodifiableSet(relationships);

        logger = context.getLogger();
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }


    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {
        FlowFile flowFile = session.get();
        if (flowFile == null) {
            return;
        }


        String env = flowFile.getAttribute(NiFiUpdateAttribute.ENV.getValue());
        String brand = flowFile.getAttribute(NiFiUpdateAttribute.BRAND.getValue());

        String locale = flowFile.getAttribute(NiFiUpdateAttribute.LOCALE.getValue());

        String apiKey = flowFile.getAttribute(NiFiUpdateAttribute.ALGOLIA_API_KEY.getValue());
        String applicationId = flowFile.getAttribute(NiFiUpdateAttribute.ALGOLIA_APP_ID.getValue());

        AlgoliaUtil algoliaUtil;
        try {
            String index_names = context.getProperty(indexes_and_replicas).getValue();
            algoliaUtil = new AlgoliaUtil(applicationId, apiKey, index_names,
                    context.getProperty(batchSize).asInteger());
            algoliaUtil.setLogger(getLogger());
            List<String> locales = algoliaUtil.getMultipleLocales(locale);


            if (env == null || locale == null || brand == null || apiKey == null
                    || applicationId == null || algoliaUtil == null) {
                getLogger().error("Missing mandatory parameters");
                session.putAttribute(flowFile, "mandatoryParameters", "error");
                session.transfer(flowFile, FAILURE_RELATIONSHIP);
            } else {

                for (int i = 0; i < locales.size(); i++) {

                    String indexName = algoliaUtil.generateIndexName(env, brand, locales.get(i));

                    try {
                        if (AlgoliaAction.setIndexSettings.toString()
                                .equals(context.getProperty(actions).getValue())) {
                            String index_settings_conf =
                                    context.getProperty(index_replicas_settings).getValue();
                            String conditionless_rule_conf =
                                    context.getProperty(conditionless_rule).getValue();
                            String renderingContent_conf =
                                    context.getProperty(grouped_renderingContent).getValue();
                            String lang_conf = context.getProperty(language_config).getValue();

                            algoliaUtil.setIndexSettings(indexName, index_settings_conf,
                                    conditionless_rule_conf, renderingContent_conf, lang_conf);
                            session.putAttribute(flowFile, "result_setIndexSettings", "success");
                        }
                    } catch (Exception e) {
                        session.putAttribute(flowFile, "result_setIndexSettings", "error");
                        throw e;
                    }

                    try {
                        if (AlgoliaAction.indexPropagation.toString()
                                .equals(context.getProperty(actions).getValue())) {
                            String start_environment =
                                    context.getProperty(propagation_start_environment).getValue();
                            algoliaUtil.propagationIndex(indexName, start_environment);
                            session.putAttribute(flowFile, "result_indexPropagation", "success");
                        }
                    } catch (Exception e) {
                        session.putAttribute(flowFile, "result_indexPropagation", "error");
                        throw e;
                    }
                    if (AlgoliaAction.indexingObjects.toString()
                            .equals(context.getProperty(actions).getValue())) {
                        String target_environment =
                                context.getProperty(index_data_target_environment).getValue();

                        try {
                            session.read(flowFile, new InputStreamCallback() {
                                @Override
                                public void process(final InputStream in) throws IOException {
                                    logger.info("Before ParseJson");
                                    JsonParser jsonParser = new JsonFactory()
                                            .createParser(new InputStreamReader(in, "UTF-8"));
                                    final ObjectMapper objectMapper = new ObjectMapper();
                                    ArrayList<Record> records = new ArrayList<Record>();
                                    CollectionType listType = objectMapper.getTypeFactory()
                                            .constructCollectionType(ArrayList.class, Record.class);
                                    records = objectMapper.readValue(jsonParser, listType);
                                    logger.info("After ParseJson");
                                    if (AlgoliaAction.indexingObjects.toString()
                                            .equals(context.getProperty(actions).getValue())) {
                                        String loadMode = context.getProperty(load_mode).getValue();
                                        algoliaUtil.performOperation(records, loadMode, indexName,
                                                target_environment);
                                    }
                                }
                            });
                            session.putAttribute(flowFile, "result_indexingObjects", "success");
                        } catch (Exception e) {
                            session.putAttribute(flowFile, "result_indexingObjects", "error");
                            throw e;
                        }
                    }
                }

                session.transfer(flowFile, SUCCESS_RELATIONSHIP);
            }

        } catch (Exception e) {
            getLogger().error("Something bad happened: " + e, e);
            session.transfer(flowFile, FAILURE_RELATIONSHIP);
        }
    }

    /**
     * @return List<PropertyDescriptor> return the descriptors
     */
    public List<PropertyDescriptor> getDescriptors() {
        return descriptors;
    }

    /**
     * @param descriptors the descriptors to set
     */
    public void setDescriptors(List<PropertyDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    /**
     * @param relationships the relationships to set
     */
    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(ComponentLog logger) {
        this.logger = logger;
    }

}

