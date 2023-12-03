package algolia.ingestor;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexNamesConf {
    
    private PrimaryIndexConf groupedIndex;
    private PrimaryIndexConf ungroupedIndex;
    private ArrayList<VirtualReplicaConf> virtualReplicas;




    /**
     * @return PrimaryIndexConf return the groupedIndex
     */
    public PrimaryIndexConf getGroupedIndex() {
        return groupedIndex;
    }

    /**
     * @param groupedIndex the groupedIndex to set
     */
    public void setGroupedIndex(PrimaryIndexConf groupedIndex) {
        this.groupedIndex = groupedIndex;
    }

    /**
     * @return PrimaryIndexConf return the ungroupedIndex
     */
    public PrimaryIndexConf getUngroupedIndex() {
        return ungroupedIndex;
    }

    /**
     * @param ungroupedIndex the ungroupedIndex to set
     */
    public void setUngroupedIndex(PrimaryIndexConf ungroupedIndex) {
        this.ungroupedIndex = ungroupedIndex;
    }

    /**
     * @return ArrayList<VirtualReplicaConf> return the virtualReplicas
     */
    public ArrayList<VirtualReplicaConf> getVirtualReplicas() {
        return virtualReplicas;
    }

    /**
     * @param virtualReplicas the virtualReplicas to set
     */
    public void setVirtualReplicas(ArrayList<VirtualReplicaConf> virtualReplicas) {
        this.virtualReplicas = virtualReplicas;
    }

}
