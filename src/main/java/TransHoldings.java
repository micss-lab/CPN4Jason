public class TransHoldings {


      String trans_long_id;
      String trans_appearing_name;
      String trans_plan_code;
      String plan_root_id;
      String travelled;


    public TransHoldings(String trans_long_id, String trans_appearing_name, String trans_plan_code,String plan_root_id) {
        this.trans_long_id = trans_long_id;
        this.trans_appearing_name = trans_appearing_name;
        this.trans_plan_code = trans_plan_code;
        this.plan_root_id=plan_root_id;
        this.travelled="";
    }

    @Override
    public String toString() {
        return "TransHoldings{" +
                "trans_long_id='" + trans_long_id + '\'' +
                ", trans_appearing_name='" + trans_appearing_name + '\'' +
                ", trans_plan_code='" + trans_plan_code + '\'' +
                ", plan_root_id='" + plan_root_id + '\'' +
                '}';
    }
}
