public class BeliefHoldings {

    String belief_name;
    String belief_value;
    String belief_type_conv;
    int belief_id;
    String var_name;
    int var_id;

    public BeliefHoldings(String var_name, int var_id) {
        this.var_name = var_name;
        this.var_id = var_id;
    }

    public String getBelief_name() {
        return belief_name;
    }

    public String getBelief_value() {
        return belief_value;
    }

    public int getBelief_id() {
        return belief_id;
    }


    public BeliefHoldings(String belief_name, String belief_value, String belief_type_conv, int belief_id, String var_name, int var_id) {
        this.belief_name = belief_name;
        this.belief_value = belief_value;
        this.belief_type_conv = belief_type_conv;
        this.belief_id = belief_id;
        this.var_name = var_name;
        this.var_id = var_id;
    }

}




