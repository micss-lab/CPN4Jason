public class PlanHoldings {

    String plan_original_id;
    String plan_long_id;
    String plan_appearing_name;
    String in_or_out;

    public PlanHoldings(String plan_original_id, String plan_long_id, String plan_appearing_name, String plan_condition, String in_or_out) {
        this.plan_original_id = plan_original_id;
        this.plan_long_id = plan_long_id;
        this.plan_appearing_name = plan_appearing_name;
        this.in_or_out = in_or_out;
        this.plan_condition = plan_condition;
    }

    public String getPlan_original_id() {
        return plan_original_id;
    }

    public void setPlan_original_id(String plan_original_id) {
        this.plan_original_id = plan_original_id;
    }

    public String getPlan_long_id() {
        return plan_long_id;
    }

    public void setPlan_long_id(String plan_long_id) {
        this.plan_long_id = plan_long_id;
    }

    public String getPlan_appearing_name() {
        return plan_appearing_name;
    }

    public void setPlan_appearing_name(String plan_appearing_name) {
        this.plan_appearing_name = plan_appearing_name;
    }

    public String getIn_or_out() {
        return in_or_out;
    }

    public void setIn_or_out(String in_or_out) {
        this.in_or_out = in_or_out;
    }

    public String getPlan_condition() {
        return plan_condition;
    }

    public void setPlan_condition(String plan_condition) {
        this.plan_condition = plan_condition;
    }

    @Override
    public String toString() {
        return "PlanHoldings{" +
                "plan_original_id='" + plan_original_id + '\'' +
                ", plan_long_id='" + plan_long_id + '\'' +
                ", plan_appearing_name='" + plan_appearing_name + '\'' +
                ", in_or_out='" + in_or_out + '\'' +
                ", plan_condition='" + plan_condition + '\'' +
                '}';
    }

    String plan_condition;


}
