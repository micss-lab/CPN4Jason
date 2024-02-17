import java.awt.font.FontRenderContext;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import jason.asSyntax.Plan;
import jason.asSyntax.PlanLibrary;
import jason.asSyntax.parser.*;
import jason.asSemantics.*;

import javax.swing.*;

public class DemoParser {

    static int ID_assign = 1000000000;
    static int ID_counter =1;

     static int init_ID=0;

     static int init_goal_t_id=0;
     static int init_goal_p_id=0;

     static int first_goal_t=0;


    static String ConditionParser(String Condition) {

        System.out.println("ALM"+Condition);



        for (int i = 0; i < Condition.length(); i++) {

        if (!(Condition.equals("true"))){

            System.out.println("ConditionZ"+Condition);

        if (Condition.contains("buildFree") || Condition.contains("dropped") || Condition.contains("colorDecided") ||
                Condition.contains("shredEndSent") || Condition.contains("colorValue")){



                        if (Condition.contains("colorDecided") ) {

                            String cont[] = Condition.split("&");
                            Condition = cont[cont.length - 1].replaceAll("\\(", "").replaceAll("\\)", "");
                        }


                        if (Condition.contains("buildFree")) {
                               Condition= Condition.replaceAll("buildFree","BF=").replaceAll("\\(","").replaceAll("\\)","");
                            System.out.println("WHATTA "+Condition);
                            }




        }


        if (Condition.charAt(i)==('&') && (  (Condition.charAt(i+1)!= 'g') || (Condition.charAt(i+1)!= 'l')   ) ){



            Condition=Condition.replace(" & "," andalso ");

            System.out.println("ebn "+i+" "+ Condition.charAt(i)+" "+Condition.charAt(i+1));
        }

        else if (Condition.charAt(i)==('|')){
            Condition=Condition.replace("|"," orelse ");
        }

        else if (Condition.charAt(i)==('\\') && Condition.charAt(i+1)==('=') && Condition.charAt(i+2)==('=')){
            Condition=Condition.replaceAll("\\\\==","&lt;&gt;");
        }

        else if (Condition.charAt(i)==('<') ){
            Condition=Condition.replaceAll("<","&lt;");
        }

        else if (Condition.charAt(i)==('>') ){
            Condition=Condition.replaceAll(">","&gt;");
        }


        else if (Condition.charAt(i)==('=') && Condition.charAt(i+1)==('=')){
            Condition=Condition.replace("==","=");
        }

        else if (Condition.charAt(i)==('K') || Condition.charAt(i)==('U') || (Condition.charAt(i)==('F')&& Condition.charAt(i-1)!=('B')) ||
                (Condition.charAt(i)==('P') && Condition.charAt(i+1)==('h'))){
            Condition=Condition.replaceAll("K|U|F|Ph","CV");
       //     System.out.println("here"+Condition);
        }

        else if (Condition.contains("CDS")){
            Condition=Condition.replace("CDS","CD");
        }

        } else {

            return "(BF,D,CD,SES,CV)";
        }

        }

      //  else if (Condition.contains("true")){
    //        Condition="";   //   if bF=true then  1`(cV,sES,cD,dp,bF) else empty
   //     }
        System.out.println("if "+Condition+" then 1` (BF,D,CD,SES,CV) else empty");



       // "if cV=5 orelse cV=3 orelse cV=4 then 1`(cV,sES,cD,dp,bF) else empty";
        return "if "+Condition+" then 1` (BF,D,CD,SES,CV) else empty";//"if "+Condition+" then 1` (cV,sES,cD,dp,bF) else empty";
    }






    public static void main(String[] args) throws Exception {
        // create a parser from a file (it could a=be  any kind of stream)



        Hashtable<String, String> places_and_ids = new Hashtable<String, String>();
        var parser = new as2j(new FileInputStream("sortAgent_paper.asl"));

        // create an Agent where to place the result of the parser
        var ag = new Agent();
        ag.initAg();

        // run the parser
        parser.agent(ag);

        // print what was parsed
        System.out.println("Initial beliefs: "+ag.getInitialBels());
        System.out.println("Initial goals: "+ag.getInitialGoals());


        BeliefHoldings[] myBH = new BeliefHoldings[ag.getInitialBels().size()];

        for (var plan: ag.getPL()) {
            System.out.println("\nPlan: "+plan.getTerm(1));
        }

        for (var belief: ag.getInitialBels()) {
            System.out.println("\n***Belief: "+belief);
        }

        for (var belief: ag.getInitialGoals()) {
            System.out.println("\n***Belief: "+belief);
        }




        String template ="<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                "<!DOCTYPE workspaceElements PUBLIC \"-//CPN//DTD CPNXML 1.0//EN\" \"http://cpntools.org/DTD/6/cpn.dtd\">\n" +
                "\n" +
                "<workspaceElements>\n" +
                "  <generator tool=\"CPN Tools\"\n" +
                "             version=\"4.0.1\"\n" +
                "             format=\"6\"/>\n" +
                "  <cpnet>\n" +
                "    <globbox>\n" +
                "      <block id=\"ID1412310166\">\n" +
                "        <id>Standard priorities</id>\n" +
                "        <ml id=\"ID1412310255\">val P_HIGH = 100;\n" +
                "          <layout>val P_HIGH = 100;</layout>\n" +
                "        </ml>\n" +
                "        <ml id=\"ID1412310292\">val P_NORMAL = 1000;\n" +
                "          <layout>val P_NORMAL = 1000;</layout>\n" +
                "        </ml>\n" +
                "        <ml id=\"ID1412310322\">val P_LOW = 10000;\n" +
                "          <layout>val P_LOW = 10000;</layout>\n" +
                "        </ml>\n" +
                "      </block>";

            template+="<block id=\"ID1\">\n" +
                    "        <id>Standard declarations</id>\n";





            int count=0;
            for (var belief: ag.getInitialBels()) {
        //    System.out.println("\n***Belief: "+belief);

                String init_belief= belief.toString().replace("[","").replace("]","");
                System.out.println("inits:"+init_belief);

                String[][] beliefs_names= new String[ag.getInitialBels().size()][];

                String belief_name=init_belief.substring(0,init_belief.toString().indexOf('('));
                String belief_value=(init_belief.toString().substring(init_belief.toString().indexOf('(')+1,init_belief.toString().indexOf(')')));

                String s1=belief_name.toString().substring(0,1).toUpperCase();
                String s2=belief_name.substring(1);
                String belief_type_conv=s1+s2;


      //          my_dict.put(belief_name,belief_value);
                System.out.println("ee"+belief_value);
                if (belief_value.equals("false") || belief_value.equals("true")){
                    belief_value = "bool";
                }
                else {
                    belief_value = "int";
                }

                //System.out.println(belief_type_conv);


                String var_name_assigner=belief_type_conv.replaceAll("[^A-Z]", "");
               // System.out.println("zz"+var_name_assigner);




                myBH[count]= new BeliefHoldings(belief_name,belief_value,belief_type_conv,(ID_assign+ID_counter),var_name_assigner,ID_assign+ID_counter+1);

                ID_counter+=2;



                template+=  "<color id=\"ID"+myBH[count].belief_id+"\">\n"+
                        "<id>"+myBH[count].belief_type_conv+"</id>\n"+
                        "<"+belief_value+"/>\n"+
                        "<layout>colset "+belief_type_conv+"="+belief_value+";</layout>\n"+
                        "</color>\n";








         //<var id="ID1412671385">
         //<type>
         //<id>Dropped</id>
         //</type>
         //<id>dp</id>
         //<layout>var dp:Dropped;</layout>
         //</var>

            count++;
            } // end of the loop.



        template+="<color id=\"ID"+ID_assign+ID_counter+"\">\n"+
           "<id>Beliefs</id>\n"+
           "<product>\n";


        for (int i = 0; i < ag.getInitialBels().size(); i++) {

            template+="<id>"+myBH[i].belief_type_conv+"</id>\n";

        }

        template+="</product>\n"+"<layout>colset Beliefs= product";


        for (int i = 0; i < ag.getInitialBels().size(); i++) {
            template+="*"+myBH[i].belief_type_conv;
        }

        template+=";</layout>\n </color>";



        for (int i = 0; i < ag.getInitialBels().size(); i++) {
            ID_counter+=1;
            template+="\n<var id=\"ID"+(ID_counter+ID_assign)+"\">\n" +
                "          <type>\n" +
                "            <id>"+myBH[i].belief_type_conv+"</id>\n" +
                "          </type>\n" +
                "          <id>"+myBH[i].var_name+"</id>\n" +
                "          <layout>var "+myBH[i].var_name+":"+myBH[i].belief_type_conv+";</layout>\n" +
                "        </var>";
        }
         // Beliefler obje olarak tutulmali.


        template+="<color id=\"ID85042\">\n" +
                "          <id>UNIT</id>\n" +
                "          <unit/>\n" +
                "          <layout>colset UNIT = unit;</layout>\n" +
                "        </color>";

        template+="<color id=\"ID1412312409\">\n" +
                "          <id>INTINF</id>\n" +
                "          <intinf/>\n" +
                "          <layout>colset INTINF = intinf;</layout>\n" +
                "        </color>";

        template+="<color id=\"ID1412312425\">\n" +
                "          <id>TIME</id>\n" +
                "          <time/>\n" +
                "          <layout>colset TIME = time;</layout>\n" +
                "        </color>";

        template+="  <color id=\"ID1412322990\">\n" +
                "          <id>REAL</id>\n" +
                "          <real/>\n" +
                "          <layout>colset REAL = real;</layout>\n" +
                "        </color>";

        template+="<color id=\"ID5\">\n" +
                "          <id>STRING</id>\n" +
                "          <string/>\n" +
                "        </color>";



        template+="</block>  \t \n" +
                    "</globbox>";



        template+="<page id=\"ID6\">\n" +
                "      <pageattr name=\"New Page\"/>";


        // Initialise the belief base  here

        ID_counter+=1;
        template+="<place id=\"ID101\">\n" +
                "        <posattr x=\"-1092.000000\"\n" +
                "                 y=\"210.000000\"/>\n" +
                "        <fillattr colour=\"White\"\n" +
                "                  pattern=\"\"\n" +
                "                  filled=\"false\"/>\n" +
                "        <lineattr colour=\"Black\"\n" +
                "                  thick=\"1\"\n" +
                "                  type=\"Solid\"/>\n" +
                "        <textattr colour=\"Black\"\n" +
                "                  bold=\"false\"/>\n" +
                "        <text>Belief Base</text>\n" +
                "        <ellipse w=\"96.000000\"\n" +
                "                 h=\"40.000000\"/>\n" +
                "        <token x=\"-10.000000\"\n" +
                "               y=\"0.000000\"/>\n" +
                "        <marking x=\"0.000000\"\n" +
                "                 y=\"0.000000\"\n" +
                "                 hidden=\"false\">\n" +
                "          <snap snap_id=\"0\"\n" +
                "                anchor.horizontal=\"0\"\n" +
                "                anchor.vertical=\"0\"/>\n" +
                "        </marking>\n" +
                "        <type id=\"ID"+(ID_counter+ID_assign)+"\">\n" +
                "          <posattr x=\"-1034.500000\"\n" +
                "                   y=\"186.000000\"/>\n" +
                "          <fillattr colour=\"White\"\n" +
                "                    pattern=\"Solid\"\n" +
                "                    filled=\"false\"/>\n" +
                "          <lineattr colour=\"Black\"\n" +
                "                    thick=\"0\"\n" +
                "                    type=\"Solid\"/>\n" +
                "          <textattr colour=\"Black\"\n" +
                "                    bold=\"false\"/>\n" +
                "          <text tool=\"CPN Tools\"\n" +
                "                version=\"4.0.1\">Beliefs</text>\n" +
                "        </type>\n" +
                "        <initmark id=\"ID"+(ID_counter+ID_assign+1)+"\">\n" +
                "          <posattr x=\"-972.000000\"\n" +
                "                   y=\"233.000000\"/>\n" +
                "          <fillattr colour=\"White\"\n" +
                "                    pattern=\"Solid\"\n" +
                "                    filled=\"false\"/>\n" +
                "          <lineattr colour=\"Black\"\n" +
                "                    thick=\"0\"\n" +
                "                    type=\"Solid\"/>\n" +
                "          <textattr colour=\"Black\"\n" +
                "                    bold=\"false\"/>\n" +
                "          <text tool=\"CPN Tools\"\n";




        template+="        version=\"4.0.1\">1`(";
        for (int i = 0; i < ag.getInitialBels().size(); i++) {
            if (myBH[i].belief_value == "bool"){
                template+="false,";
            }
            else
                template+="0,";
        }
        template=template.substring(0,template.length()-1); // erase the last comma.



        template+=")</text>\n" +
                "        </initmark>\n" +
                "      </place>";
        ID_counter+=2;

        ////////////////////////////////////////////////////

        ID_counter+=1000;

        init_ID=ID_assign+ID_counter;
        init_goal_t_id =init_ID;
// Create the special transition "init"
        template+=" <trans id=\"ID"+(init_ID)+"\"\n" +
                "             explicit=\"false\">\n" +
                "        <posattr x=\"-1092.000000\"\n" +
                "                 y=\"72.000000\"/>\n" +
                "        <fillattr colour=\"White\"\n" +
                "                  pattern=\"\"\n" +
                "                  filled=\"false\"/>\n" +
                "        <lineattr colour=\"Black\"\n" +
                "                  thick=\"1\"\n" +
                "                  type=\"solid\"/>\n" +
                "        <textattr colour=\"Black\"\n" +
                "                  bold=\"false\"/>\n" +
                "        <text>init</text>\n" +
                "        <box w=\"210.000000\"\n" +
                "             h=\"45.000000\"/>\n" +
                "        <binding x=\"7.200000\"\n" +
                "                 y=\"-3.000000\"/>\n";

        template+=
                "        <cond id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                        "          <posattr x=\"-1131.000000\"\n" +
                        "                   y=\"103.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </cond>\n";

        ID_counter+=1;
        template+=
                "        <time id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                        "          <posattr x=\"-1047.500000\"\n" +
                        "                   y=\"103.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </time>\n";
        ID_counter+=1;
        template+=  "        <code id=\"ID"+((ID_assign-ID_counter)*101)+"\">\n" +
                "          <posattr x=\"-1027.500000\"\n" +
                "                   y=\"20.000000\"/>\n" +
                "          <fillattr colour=\"White\"\n" +
                "                    pattern=\"Solid\"\n" +
                "                    filled=\"false\"/>\n" +
                "          <lineattr colour=\"Black\"\n" +
                "                    thick=\"0\"\n" +
                "                    type=\"Solid\"/>\n" +
                "          <textattr colour=\"Black\"\n" +
                "                    bold=\"false\"/>\n" +
                "          <text tool=\"CPN Tools\"\n" +
                "                version=\"4.0.1\"/>\n" +
                "        </code>\n";

        ID_counter+=1;
        template+=
                "        <priority id=\"ID"+((ID_assign-ID_counter)*102)+"\">\n" +
                        "          <posattr x=\"-1160.000000\"\n" +
                        "                   y=\"41.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </priority>\n" +
                        "      </trans>";



////////////////////////////////////////////////////// BIND INIT AND THE BELIEF BASE ///////////////////////

        template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                "           orientation=\"PtoT\"\n" +
                "           order=\"1\">\n" +
                "        <posattr x=\"0.000000\"\n" +
                "                 y=\"0.000000\"/>\n" +
                "        <fillattr colour=\"White\"\n" +
                "                  pattern=\"\"\n" +
                "                  filled=\"false\"/>\n" +
                "        <lineattr colour=\"Black\"\n" +
                "                  thick=\"1\"\n" +
                "                  type=\"Solid\"/>\n" +
                "        <textattr colour=\"Black\"\n" +
                "                  bold=\"false\"/>\n" +
                "        <arrowattr headsize=\"1.200000\"\n" +
                "                   currentcyckle=\"2\"/>\n" +
                "        <transend idref=\"ID"+init_ID+"\"/>\n" +
                "        <placeend idref=\"ID"+101+"\"/>\n";




        ID_counter+=10;
        template+=
                "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                        "          <posattr x=\"-1092.000000\"\n" +
                        "                   y=\"72.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                        "        </annot>\n" +
                        "      </arc>";

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




/////////////////////////////////////////////////////////////////////////
        var place_id_temp=0;
        for (int i = 0; i <ag.getInitialGoals().size() ; i++) {
            ID_counter+=1;

            place_id_temp=ID_counter+ID_assign;
            init_goal_p_id=place_id_temp;
        //    System.out.println(init_goal_p_id+"init pppp");
            template+="<place id=\"ID"+(ID_counter+ID_assign)+"\">\n" +
                    "        <posattr x=\"-"+(882+(i*20))+".000000\"\n" +
                    "                 y=\""+(72+(i*20))+".000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <text>";

                    template+="to"+ag.getInitialGoals().toArray()[i];
                    places_and_ids.put("to"+ag.getInitialGoals().toArray()[i],"ID"+(ID_counter+ID_assign));
                    template+="</text>\n" +
                    "        <ellipse w=\"60.000000\"\n" +
                    "                 h=\"40.000000\"/>\n" +
                    "        <token x=\"-10.000000\"\n" +
                    "               y=\"0.000000\"/>\n" +
                    "        <marking x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"\n" +
                    "                 hidden=\"false\">\n" +
                    "          <snap snap_id=\"0\"\n" +
                    "                anchor.horizontal=\"0\"\n" +
                    "                anchor.vertical=\"0\"/>\n" +
                    "        </marking>\n";

                    ID_counter+=1;
                   template+="<type id=\"ID"+(ID_counter+ID_assign)+"\">\n" +
                    "          <posattr x=\"-"+(920+(i*20))+".500000\"\n" +
                    "                   y=\""+(72+(i*20))+".000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\">Beliefs</text>\n" +
                    "        </type>\n";
                    ID_counter+=1;
                   template+="<initmark id=\"ID"+(ID_counter+ID_assign)+"\">\n" +
                    "          <posattr x=\"-825.000000\"\n" +
                    "                   y=\"95.000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\"/>\n" +
                    "        </initmark>\n" +
                    "      </place>";

                   ///////////////////////////////////////// BIND INIT TO THE INITIAL GOALS ///////////////////////////

            template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                    "           orientation=\"TtoP\"\n" +
                    "           order=\"1\">\n" +
                    "        <posattr x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <arrowattr headsize=\"1.200000\"\n" +
                    "                   currentcyckle=\"2\"/>\n" +
                    "        <transend idref=\"ID"+init_ID+"\"/>\n" +
                    "        <placeend idref=\"ID"+place_id_temp+"\"/>\n";





            ID_counter+=10;
            template+=
                    "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                            "          <posattr x=\""+(56+(i*50))+"\"\n" +
                            "                   y=\""+(410+(i*50))+"+.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                            "        </annot>\n" +
                            "      </arc>";

////////////////////////////////////////////////////////////////////////////////////////////////////////
            //HERE

            ID_counter+=1;
            var trans_ID_temp=ID_counter+ID_assign;
            first_goal_t=trans_ID_temp;


            template+=" <trans id=\"ID"+(ID_counter+ID_assign)+"\"\n" +
                    "             explicit=\"false\">\n" +
                    "        <posattr x=\"-552.000000\"\n" +
                    "                 y=\"72.000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <text>"+"to"+ag.getInitialGoals().toArray()[i]+"</text>\n" +
                    "        <box w=\"210.000000\"\n" +
                    "             h=\"45.000000\"/>\n" +
                    "        <binding x=\"7.200000\"\n" +
                    "                 y=\"-3.000000\"/>\n";

            template+=
                    "        <cond id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                            "          <posattr x=\"-1131.000000\"\n" +
                            "                   y=\"103.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\"/>\n" +
                            "        </cond>\n";

            ID_counter+=1;
            template+=
                    "        <time id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                            "          <posattr x=\"-1047.500000\"\n" +
                            "                   y=\"103.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\"/>\n" +
                            "        </time>\n";
            ID_counter+=1;
            template+=  "        <code id=\"ID"+((ID_assign-ID_counter)*101)+"\">\n" +
                    "          <posattr x=\"-1027.500000\"\n" +
                    "                   y=\"20.000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\"/>\n" +
                    "        </code>\n";

            ID_counter+=1;
            template+=
                    "        <priority id=\"ID"+((ID_assign-ID_counter)*102)+"\">\n" +
                            "          <posattr x=\"-1160.000000\"\n" +
                            "                   y=\"41.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\"/>\n" +
                            "        </priority>\n" +
                            "      </trans>";


 //////////////

            template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                    "           orientation=\"PtoT\"\n" +
                    "           order=\"1\">\n" +
                    "        <posattr x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <arrowattr headsize=\"1.200000\"\n" +
                    "                   currentcyckle=\"2\"/>\n" +
                    "        <transend idref=\"ID"+trans_ID_temp+"\"/>\n" +
                    "        <placeend idref=\"ID"+place_id_temp+"\"/>\n";


            ID_counter+=10;


            ID_counter+=10;
            template+=
                    "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                            "          <posattr x=\"-552.000000\"\n" +
                            "                   y=\"72.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                            "        </annot>\n" +
                            "      </arc>";




        }










        ID_counter+=10;


        int plan_occ=2;
        String plan_name_prev ="";
        String plan_name="";
        String assigned_name="";

        PlanHoldings[] myPH = new PlanHoldings[ag.getPL().size()];

        ArrayList<PlanHoldings> MiDPH = new ArrayList<>();

        TransHoldings[] myTrans = new TransHoldings[ag.getPL().size()];

        ArrayList<TransHoldings> MiDTrans = new ArrayList<>();

        int PHCounter=0;

        for (var plan: ag.getPL()) {


      //     System.out.println(plan.getTerm(1).toString());
         //   System.out.println("\nPlan: "+plan.getTerm(1).toString().replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]",""));
            ID_counter+=1;
            var in_or_out_decide="";


                if (plan.getTerm(1).toString().contains("+") || plan.getTerm(1).toString().contains("-")) {
                    in_or_out_decide = "in";

                }else{
                    in_or_out_decide="out";
                }

                if (plan.getTerm(1).toString().contains("(")){

                    plan_name=plan.getTerm(1).toString().substring(0,plan.getTerm(1).toString().indexOf("(")).replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","");

                }

                else{

                    plan_name=plan.getTerm(1).toString().replaceAll("[^a-zA-Z0-9_-]", "");
                }


            if (plan_name_prev.equals(plan_name))
            {
                plan_occ+=1;





                    places_and_ids.put(plan_name + plan_occ, "ID" + ID_counter + ID_assign);
                    System.out.println(plan_name+plan_occ+" nms");

                assigned_name=plan_name + plan_occ;



            }

            else
            {
                plan_occ=0;

                if (plan.getTerm(1).toString().charAt(0)=='-'){
                    places_and_ids.put((plan_name+'F').replaceAll("[-+.^:,]",""),"ID"+ID_counter+ID_assign);
                    System.out.println((plan_name+'F').replaceAll("[-+.^:,]",""));


                    assigned_name=(plan_name+'F').replaceAll("[-+.^:,]","");
                }
                else
                {
                    places_and_ids.put(plan_name+plan_occ,"ID"+(ID_counter+ID_assign));

                    assigned_name=plan_name+plan_occ;

                    System.out.println(plan_name+plan_occ);
                }




            }

            plan_name_prev=plan_name;



            myPH[PHCounter] = new PlanHoldings(plan.getTerm(0).toString().substring(0,plan.getTerm(0).toString().indexOf("[")),places_and_ids.get(assigned_name),assigned_name,plan.getTerm(2).toString(),in_or_out_decide);

            if (myPH[PHCounter].in_or_out.equals("out")){

              //  System.out.println("plan durumu"+myPH[PHCounter].plan_appearing_name);
            }



            // Generate the places
            if (!(plan_name.equals(""))){



            template+=" <place id=\""+myPH[PHCounter].plan_long_id+"\">\n" +
                    "        <posattr x=\""+(221)+".000000\"\n" +
                    "                 y=\""+(-519+(PHCounter*-50))+".000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <text>"+myPH[PHCounter].plan_appearing_name+"</text>\n" +
                    "        <ellipse w=\"132.000000\"\n" +
                    "                 h=\"56.000000\"/>\n" +
                    "        <token x=\"-10.000000\"\n" +
                    "               y=\"0.000000\"/>\n" +
                    "        <marking x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"\n" +
                    "                 hidden=\"false\">\n" +
                    "          <snap snap_id=\"0\"\n" +
                    "                anchor.horizontal=\"0\"\n" +
                    "                anchor.vertical=\"0\"/>\n" +
                    "        </marking>\n" +
                    "        <type id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                    "          <posattr x=\""+(221)+".500000\"\n" +
                    "                   y=\""+(-519+(PHCounter*-50))+".000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\">Beliefs</text>\n" +
                    "        </type>\n" +
                    "        <initmark id=\"ID"+((ID_assign-ID_counter)*10)+"\">\n" +
                    "          <posattr x=\"573.000000\"\n" +
                    "                   y=\"71.000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\"/>\n" +
                    "        </initmark>\n" +
                    "      </place>";

                PHCounter+=1;
            }
        }


        //System.out.println(places_and_ids.keys());

   /*     Enumeration<String> keys = places_and_ids.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            System.out.println("Key: " + key + ", Value: " + places_and_ids.get(key));
        }
*/

















/////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create Transtions

        var plann=new Plan();

        boolean last_index=false;
        String s1="",s2="",original_term_name="";
        for (int i = 0; i <ag.getPL().size() ; i++) {
            var count_trans=0;
            // String trans_long_id, String trans_target_id, String trans_source_id, String trans_appearing_name

            plann=ag.getPL().getPlans().get(i);



            while (plann.getTerm(count_trans)!=null) {
                System.out.println(plann.getTerm(count_trans));
                count_trans++;




              //  if (plann.getTerm(count_trans-1).toString().indexOf("+-")>0){


            //        System.out.println("Captured Belief Change"+plann.getTerm(count_trans-1).toString().indexOf("+-"));
            //    }







                if (plann.getTerm(count_trans)==null){

                        if ((plann.getTerm(1).toString().indexOf("("))>0) {
                            s1 =plann.getTerm(1).toString().substring(0,plann.getTerm(1).toString().indexOf("(")).replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","") ;


                        }
                        else{
                            s1=plann.getTerm(1).toString().replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","");

                        }






                        if (plann.getTerm(count_trans-1).toString().indexOf('!')>0){
                            int index=plann.getTerm(count_trans-1).toString().indexOf('!');

                            original_term_name=plann.getTerm(count_trans-1).toString();
                            s2=plann.getTerm(count_trans-1).toString().substring(index+1);

                            System.out.println("s2_names 0"+s2);


                            if (s2.indexOf("(")>0){

                                s2= s2.substring(0,s2.indexOf("(")).replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","");

                                System.out.println("s2_names 1"+s2);
                            }

                            else{

                                s2.replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","");
                            }


                        }
                        else{

                             s2=s1+"Back"; //replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]","");

                            //    System.out.println("Terminate?"+plann.getTerm(count_trans-1).toString());

                        }






                //        System.out.println("ccx"+s1+"***To***"+s2+"  hhh "+ plann.getTerm(0));



                        }

            }
           System.out.println("\n");





                //
//            System.out.println(plan.getTerm(plan_count));
//            plan_count+=1;

            ID_counter+=1;


    //        System.out.println("awdfwaad"+plann.getTerm(0).toString()+"awwaf");

            for (int j = 0; j < myPH.length; j++) {

                if (myPH[i].plan_original_id.equals( plann.getTerm(0).toString().substring(0,plann.getTerm(0).toString().indexOf('[')))){

                    myTrans[i]=new TransHoldings(("ID"+(ID_assign+ID_counter)),myPH[i].plan_appearing_name+"To"+s2, plann.getTerm(0).toString().substring(0,plann.getTerm(0).toString().indexOf('[')),myPH[i].plan_original_id);

                }




            }




            s1="";
            s2="";


         //   System.out.println("lll"+myTrans[i].trans_plan_code);


        // Create the transitions

        template+=" <trans id=\""+(myTrans[i].trans_long_id)+"\"\n" +
                "             explicit=\"false\">\n" +
                "        <posattr x=\"-744.000000\"\n" +
                "                 y=\"-"+(600+(35*i))+".000000\"/>\n" +
                "        <fillattr colour=\"White\"\n" +
                "                  pattern=\"\"\n" +
                "                  filled=\"false\"/>\n" +
                "        <lineattr colour=\"Black\"\n" +
                "                  thick=\"1\"\n" +
                "                  type=\"solid\"/>\n" +
                "        <textattr colour=\"Black\"\n" +
                "                  bold=\"false\"/>\n" +
                "        <text>"+(myTrans[i].trans_appearing_name)+"</text>\n" +
                "        <box w=\"210.000000\"\n" +
                "             h=\"45.000000\"/>\n" +
                "        <binding x=\"7.200000\"\n" +
                "                 y=\"-3.000000\"/>\n";

        //    System.out.println("trans_name"+myTrans[i].trans_appearing_name);

        template+=
                "        <cond id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                        "          <posattr x=\"-581.000000\"\n" +
                        "                   y=\"76.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </cond>\n";

        ID_counter+=1;
        template+=
                "        <time id=\"ID"+((ID_assign-ID_counter)*100)+"\">\n" +
                        "          <posattr x=\"-581.500000\"\n" +
                        "                   y=\"78.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </time>\n";
        ID_counter+=1;
        template+=  "        <code id=\"ID"+((ID_assign-ID_counter)*101)+"\">\n" +
                "          <posattr x=\"-566.500000\"\n" +
                "                   y=\"73.000000\"/>\n" +
                "          <fillattr colour=\"White\"\n" +
                "                    pattern=\"Solid\"\n" +
                "                    filled=\"false\"/>\n" +
                "          <lineattr colour=\"Black\"\n" +
                "                    thick=\"0\"\n" +
                "                    type=\"Solid\"/>\n" +
                "          <textattr colour=\"Black\"\n" +
                "                    bold=\"false\"/>\n" +
                "          <text tool=\"CPN Tools\"\n" +
                "                version=\"4.0.1\"/>\n" +
                "        </code>\n";

        ID_counter+=1;
        template+=
                "        <priority id=\"ID"+((ID_assign-ID_counter)*102)+"\">\n" +
                        "          <posattr x=\"-560.000000\"\n" +
                        "                   y=\"71.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </priority>\n" +
                        "      </trans>";
        }
//////////////////////////////////////////// ROOT ARCS Transition to Place /////////////////////////////
        ID_counter+=10;
        String t_place_id="";
        String t_trans_id="";
        for (int i = 0; i < myPH.length ; i++) {
            for (int j = 0; j < myPH.length; j++) {

                //    System.out.println("**********"+myPH[j].plan_appearing_name.substring(myPH[j].plan_appearing_name.indexOf("To")+1));







                if (myTrans[i].trans_appearing_name.substring(0,myTrans[i].trans_appearing_name.indexOf("To")).contains(myPH[j].plan_appearing_name) && !(myTrans[j].trans_appearing_name.contains("Out"))) {


                    //System.out.println("names "+myTrans[i].trans_appearing_name+"->"+myPH[j].plan_appearing_name);
                    //ColorBucket P - T - ColorBucket1TOtoPush


                    ID_counter+=10;
/*
                    template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                            "           orientation=\"PtoT\"\n" +
                            "           order=\"1\">\n" +
                            "        <posattr x=\"10.000000\"\n" +
                            "                 y=\"10.000000\"/>\n" +
                            "        <fillattr colour=\"White\"\n" +
                            "                  pattern=\"\"\n" +
                            "                  filled=\"false\"/>\n" +
                            "        <lineattr colour=\"Black\"\n" +
                            "                  thick=\"1\"\n" +
                            "                  type=\"Solid\"/>\n" +
                            "        <textattr colour=\"Black\"\n" +
                            "                  bold=\"false\"/>\n" +
                            "        <arrowattr headsize=\"1.200000\"\n" +
                            "                   currentcyckle=\"2\"/>\n" +
                            "        <transend idref=\""+myTrans[i].trans_long_id+"\"/>\n" +
                            "        <placeend idref=\""+myPH[j].plan_long_id+"\"/>\n";

                    ID_counter+=10;
                    template+=      "        <bendpoint id=\"ID"+(ID_assign-ID_counter)+"\"\n" +
                            "                   serial=\"1\">\n" +
                            "          <posattr x=\"-600.000000\"\n" +
                            "                   y=\"-600.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "        </bendpoint>\n";

                    ID_counter+=10;
                    template+=
                            "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                    "          <posattr x=\"-16.000000\"\n" +
                                    "                   y=\"-400.000000\"/>\n" +
                                    "          <fillattr colour=\"White\"\n" +
                                    "                    pattern=\"Solid\"\n" +
                                    "                    filled=\"false\"/>\n" +
                                    "          <lineattr colour=\"Black\"\n" +
                                    "                    thick=\"0\"\n" +
                                    "                    type=\"Solid\"/>\n" +
                                    "          <textattr colour=\"Black\"\n" +
                                    "                    bold=\"false\"/>\n" +
                                    "          <text tool=\"CPN Tools\"\n" +
                                    "                version=\"4.0.1\">10`(BF,D,CD,SES,CV)</text>\n" +
                                    "        </annot>\n" +
                                    "      </arc>";




*/






                    // burada kaldim
             //         System.out.println("salt "+myTrans[i].trans_appearing_name.substring(myTrans[i].trans_appearing_name.indexOf("T"))+" "+ myPH[j].plan_appearing_name+" zz ");


                }
            }
        }


/*
        template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                "           orientation=\"TtoP\"\n" +
                "           order=\"1\">\n" +
                "        <posattr x=\"0.000000\"\n" +
                "                 y=\"0.000000\"/>\n" +
                "        <fillattr colour=\"White\"\n" +
                "                  pattern=\"\"\n" +
                "                  filled=\"false\"/>\n" +
                "        <lineattr colour=\"Black\"\n" +
                "                  thick=\"1\"\n" +
                "                  type=\"Solid\"/>\n" +
                "        <textattr colour=\"Black\"\n" +
                "                  bold=\"false\"/>\n" +
                "        <arrowattr headsize=\"1.200000\"\n" +
                "                   currentcyckle=\"2\"/>\n" +
                "        <transend idref=\""+t_trans_id+"\"/>\n" +
                "        <placeend idref=\""+t_place_id+"\"/>\n";
        ID_counter+=10;
        template+= "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                "          <posattr x=\"19.000000\"\n" +
                "                   y=\"-129.000000\"/>\n" +
                "          <fillattr colour=\"White\"\n" +
                "                    pattern=\"Solid\"\n" +
                "                    filled=\"false\"/>\n" +
                "          <lineattr colour=\"Black\"\n" +
                "                    thick=\"0\"\n" +
                "                    type=\"Solid\"/>\n" +
                "          <textattr colour=\"Black\"\n" +
                "                    bold=\"false\"/>\n" +
                "          <text tool=\"CPN Tools\"\n" +
                "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                "        </annot>\n" +
                "      </arc>\n";

        }
*/
///////////////////////////////////
/*
        for (int i = 0; i <myPH.length ; i++) {

            if (myPH[i].plan_original_id == myTrans[i].trans_plan_code);
         //   System.out.println("azx"+myPH[i].plan_long_id);
      //      System.out.println(myPH[i].plan_appearing_name+"->"+ myTrans[i].trans_appearing_name);

            //      myPH[i].plan_appearing_name
            ID_counter+=10;

            template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                    "           orientation=\"PtoT\"\n" +
                    "           order=\"1\">\n" +
                    "        <posattr x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <arrowattr headsize=\"1.200000\"\n" +
                    "                   currentcyckle=\"2\"/>\n" +
                    "        <transend idref=\""+myTrans[i].trans_long_id+"\"/>\n" +
                    "        <placeend idref=\""+myPH[i].plan_long_id+"\"/>\n";

            ID_counter+=10;
            template+=      "        <bendpoint id=\"ID"+(ID_assign-ID_counter)+"\"\n" +
                            "                   serial=\"1\">\n" +
                            "          <posattr x=\"-126.000000\"\n" +
                            "                   y=\"-252.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "        </bendpoint>\n";

            ID_counter+=10;
            template+=
                    "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                            "          <posattr x=\"-96.000000\"\n" +
                            "                   y=\"-263.000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                            "        </annot>\n" +
                            "      </arc>";

        }

*/


        ID_counter+=1000;

var planns = ag.getPL();
String decide="";
String belief_change="CD";

String thePattern = "[^A-Za-z0-9]+";
ArrayList<String> portions;

//boolean isFound = Pattern.compile(thePattern).matcher(theInput).find();

        for (int i = 0; i <planns.getPlans().size() ; i++) {
           // for (int j = 3; j <planns.getPlans().get(i).getTermsSize() ; j++) {


             //  decide= planns.getPlans().get(7).getTerm(3).toString();s


            if (planns.getPlans().get(i).toString().contains(";")){

            portions = new ArrayList<String>(Arrays.asList(planns.getPlans().get(i).toString().substring(planns.getPlans().get(i).toString().indexOf("<-")+2).split(";")));


                for (int j = 0; j < portions.size(); j++) {
                    if ( portions!=null && portions.get(j).toString().contains("-+") )
                        System.out.println("Captured Belief Change"+ portions.get(j).toString());

                       if (portions.get(j).toString().contains("colorDecided")){

                           belief_change="not CD";


                       }


                }

            }

            else {

                portions = new ArrayList<String>();
            }

         //   System.out.println("what is decide"+planns.getPlans().get(i).getTerm(3).toString());

        //        System.out.println("what is decide"+portions[0]);

            for (int j = 0; j < portions.size(); j++) {

          //    System.out.println("what is portions "+j+" "+portions.get(j));



               if ((portions.get(j).contains("-") | portions.get(j).contains ("+")| portions.get(j).contains("?") | portions.get(j).contains("!")|
                       portions.get(j).contains("(") | portions.get(j).contains(")")| portions.get(j).contains(".")))
               {
                //   System.out.println("STATE Z"+planns.getPlans().get(i));

                   if (portions.get(j).contains ("!")){

                       System.out.println("zzzm"+portions.get(j).toString());

                       ID_counter+=10;
                       MiDPH.add(new PlanHoldings(planns.getPlans().get(i).getTerm(0).toString().substring(0,planns.getPlans().get(i).getTerm(0).toString().indexOf('[')),
                               ("ID"+ID_counter+ID_assign).toString(),
                               "To"+portions.get(j).replaceAll("[-+.^:,]",
                                       "").strip().replaceAll("[^a-zA-Z0-9_-]", ""),planns.getPlans().get(i).getTerm(2).toString(),"mid"));



                   }


               }


               else {


             //      System.out.println("Thedetected"+portions.get(j));


                   ID_counter+=10;

                    var place_id =ID_counter+ID_assign;     // GO GREEN POSITIONLAR
                   template+=" <place id=\"ID"+(place_id)+"\">\n" +
                           "        <posattr x=\""+(-518)+".000000\"\n" +
                           "                 y=\""+(-356+(-35*i))+".000000\"/>\n" +
                           "        <fillattr colour=\"White\"\n" +
                           "                  pattern=\"\"\n" +
                           "                  filled=\"false\"/>\n" +
                           "        <lineattr colour=\"Black\"\n" +
                           "                  thick=\"1\"\n" +
                           "                  type=\"Solid\"/>\n" +
                           "        <textattr colour=\"Black\"\n" +
                           "                  bold=\"false\"/>\n" +
                           "        <text>"+portions.get(j)+i+"</text>\n" +
                           "        <ellipse w=\"132.000000\"\n" +
                           "                 h=\"56.000000\"/>\n" +
                           "        <token x=\"-10.000000\"\n" +
                           "               y=\"0.000000\"/>\n" +
                           "        <marking x=\"0.000000\"\n" +
                           "                 y=\"0.000000\"\n" +
                           "                 hidden=\"false\">\n" +
                           "          <snap snap_id=\"0\"\n" +
                           "                anchor.horizontal=\"0\"\n" +
                           "                anchor.vertical=\"0\"/>\n" +
                           "        </marking>\n" +
                           "        <type id=\"ID"+(ID_assign-ID_counter)*116+"\">\n" +
                           "          <posattr x=\""+(-523)+".500000\"\n" +
                           "                   y=\""+(-356+(-35*i))+".000000\"/>\n" +
                           "          <fillattr colour=\"White\"\n" +
                           "                    pattern=\"Solid\"\n" +
                           "                    filled=\"false\"/>\n" +
                           "          <lineattr colour=\"Black\"\n" +
                           "                    thick=\"0\"\n" +
                           "                    type=\"Solid\"/>\n" +
                           "          <textattr colour=\"Black\"\n" +
                           "                    bold=\"false\"/>\n" +
                           "          <text tool=\"CPN Tools\"\n" +
                           "                version=\"4.0.1\">Beliefs</text>\n" +
                           "        </type>\n" +
                           "        <initmark id=\"ID"+((ID_assign-ID_counter)*117)+"\">\n" +
                           "          <posattr x=\"273.000000\"\n" +
                           "                   y=\"71.000000\"/>\n" +
                           "          <fillattr colour=\"White\"\n" +
                           "                    pattern=\"Solid\"\n" +
                           "                    filled=\"false\"/>\n" +
                           "          <lineattr colour=\"Black\"\n" +
                           "                    thick=\"0\"\n" +
                           "                    type=\"Solid\"/>\n" +
                           "          <textattr colour=\"Black\"\n" +
                           "                    bold=\"false\"/>\n" +
                           "          <text tool=\"CPN Tools\"\n" +
                           "                version=\"4.0.1\"/>\n" +
                           "        </initmark>\n" +
                           "      </place>";


                   ID_counter+=10;


                //////////////////////// GENERATE THE CORRESPONDING TRANSITION PART 1 - A////////////////////////////////////////
                            var trans_ID = ID_counter+ID_assign;

                   template+=" <trans id=\"ID"+(trans_ID)+"\"\n" +
                           "             explicit=\"false\">\n" +
                           "        <posattr x=\"-112.000000\"\n" +
                           "                 y=\""+(-50*i)+".000000\"/>\n" +
                           "        <fillattr colour=\"White\"\n" +
                           "                  pattern=\"\"\n" +
                           "                  filled=\"false\"/>\n" +
                           "        <lineattr colour=\"Black\"\n" +
                           "                  thick=\"1\"\n" +
                           "                  type=\"solid\"/>\n" +
                           "        <textattr colour=\"Black\"\n" +
                           "                  bold=\"false\"/>\n" +
                           "        <text>"+(planns.getPlans().get(i).getTerm(1).toString().replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]",""))+"To"+portions.get(j).strip()+i+"</text>\n" +
                           "        <box w=\"210.000000\"\n" +
                           "             h=\"45.000000\"/>\n" +
                           "        <binding x=\"7.200000\"\n" +
                           "                 y=\"-3.000000\"/>\n";


                   System.out.println("PROMPT"+(planns.getPlans().get(i).getTerm(1).toString().replaceAll("[^a-zA-Z0-9_-]", "").replaceAll("[-+.^:,]",""))+"To"+portions.get(j).strip()+i);

                   template+=
                           "        <cond id=\"ID"+(((ID_counter+ID_assign))*100)+"\">\n" +
                                   "          <posattr x=\"-581.000000\"\n" +
                                   "                   y=\"76.000000\"/>\n" +
                                   "          <fillattr colour=\"White\"\n" +
                                   "                    pattern=\"Solid\"\n" +
                                   "                    filled=\"false\"/>\n" +
                                   "          <lineattr colour=\"Black\"\n" +
                                   "                    thick=\"0\"\n" +
                                   "                    type=\"Solid\"/>\n" +
                                   "          <textattr colour=\"Black\"\n" +
                                   "                    bold=\"false\"/>\n" +
                                   "          <text tool=\"CPN Tools\"\n" +
                                   "                version=\"4.0.1\"/>\n" +
                                   "        </cond>\n";

                   ID_counter+=1;
                   template+=
                           "        <time id=\"ID"+(((ID_counter+ID_assign))*100)+"\">\n" +
                                   "          <posattr x=\"-581.500000\"\n" +
                                   "                   y=\"78.000000\"/>\n" +
                                   "          <fillattr colour=\"White\"\n" +
                                   "                    pattern=\"Solid\"\n" +
                                   "                    filled=\"false\"/>\n" +
                                   "          <lineattr colour=\"Black\"\n" +
                                   "                    thick=\"0\"\n" +
                                   "                    type=\"Solid\"/>\n" +
                                   "          <textattr colour=\"Black\"\n" +
                                   "                    bold=\"false\"/>\n" +
                                   "          <text tool=\"CPN Tools\"\n" +
                                   "                version=\"4.0.1\"/>\n" +
                                   "        </time>\n";
                   ID_counter+=1;
                   template+=  "        <code id=\"ID"+((ID_assign-ID_counter)*101)+"\">\n" +
                           "          <posattr x=\"-566.500000\"\n" +
                           "                   y=\"73.000000\"/>\n" +
                           "          <fillattr colour=\"White\"\n" +
                           "                    pattern=\"Solid\"\n" +
                           "                    filled=\"false\"/>\n" +
                           "          <lineattr colour=\"Black\"\n" +
                           "                    thick=\"0\"\n" +
                           "                    type=\"Solid\"/>\n" +
                           "          <textattr colour=\"Black\"\n" +
                           "                    bold=\"false\"/>\n" +
                           "          <text tool=\"CPN Tools\"\n" +
                           "                version=\"4.0.1\"/>\n" +
                           "        </code>\n";

                   ID_counter+=1;
                   template+=
                           "        <priority id=\"ID"+((ID_assign-ID_counter)*102)+"\">\n" +
                                   "          <posattr x=\"-560.000000\"\n" +
                                   "                   y=\"71.000000\"/>\n" +
                                   "          <fillattr colour=\"White\"\n" +
                                   "                    pattern=\"Solid\"\n" +
                                   "                    filled=\"false\"/>\n" +
                                   "          <lineattr colour=\"Black\"\n" +
                                   "                    thick=\"0\"\n" +
                                   "                    type=\"Solid\"/>\n" +
                                   "          <textattr colour=\"Black\"\n" +
                                   "                    bold=\"false\"/>\n" +
                                   "          <text tool=\"CPN Tools\"\n" +
                                   "                version=\"4.0.1\"/>\n" +
                                   "        </priority>\n" +
                                   "      </trans>";


                ID_counter+=10;
//////////////////////////////////////// GENERATE THE CORRESPONDING ARC T-TO-P ///////////////////////////////////////////////////////////////////
                template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                        "           orientation=\"TtoP\"\n" +
                        "           order=\"1\">\n" +
                        "        <posattr x=\"0.000000\"\n" +
                        "                 y=\"0.000000\"/>\n" +
                        "        <fillattr colour=\"White\"\n" +
                        "                  pattern=\"\"\n" +
                        "                  filled=\"false\"/>\n" +
                        "        <lineattr colour=\"Black\"\n" +
                        "                  thick=\"1\"\n" +
                        "                  type=\"Solid\"/>\n" +
                        "        <textattr colour=\"Black\"\n" +
                        "                  bold=\"false\"/>\n" +
                        "        <arrowattr headsize=\"1.200000\"\n" +
                        "                   currentcyckle=\"2\"/>\n" +
                        "        <transend idref=\"ID"+trans_ID+"\"/>\n" +
                        "        <placeend idref=\"ID"+place_id+"\"/>\n";
                ID_counter+=10;
                template+= "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                        "          <posattr x=\""+(292+(i*20))+".000000\"\n" +
                        "                   y=\""+(-50*i)+".000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\">(BF,D,"+belief_change+",SES,CV)</text>\n" +
                        "        </annot>\n" +
                        "      </arc>\n";


                belief_change="CD";





//////////////////////////////////////// GENERATE THE CORRESPONDING ARC P-TO-T ///////////////////////////////////////////////////////////////////
                   ID_counter+=10;

                   String match_ID = "";
                   for (int k = 0; k < planns.getPlans().size(); k++) {

                       System.out.println("TERMS X"+i+" "+ portions.get(j)+ " I "+planns.getPlans().get(i).getTerm(0)+"TERMS"+planns.getPlans().get(i).getTerm(1));

                       if (planns.getPlans().get(i).getTerm(0).toString().substring(0,planns.getPlans().get(i).getTerm(0).toString().indexOf('[')).equals(myTrans[k].plan_root_id)){

                        //  System.out.println("amanin "+myPH[k].plan_appearing_name);
                           match_ID=myPH[k].plan_long_id;
                           myPH[k].in_or_out="done";
                  //         myPH[k].in_or_out="taken";

                           System.out.println("appear"+myPH[k].plan_appearing_name);


                      //     System.out.println("Match Names Place "+myPH[k].plan_appearing_name);
                       }
                   }

                   template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                           "           orientation=\"PtoT\"\n" +
                           "           order=\"1\">\n" +
                           "        <posattr x=\"0.000000\"\n" +
                           "                 y=\"0.000000\"/>\n" +
                           "        <fillattr colour=\"White\"\n" +
                           "                  pattern=\"\"\n" +
                           "                  filled=\"false\"/>\n" +
                           "        <lineattr colour=\"Black\"\n" +
                           "                  thick=\"1\"\n" +
                           "                  type=\"Solid\"/>\n" +
                           "        <textattr colour=\"Black\"\n" +
                           "                  bold=\"false\"/>\n" +
                           "        <arrowattr headsize=\"1.200000\"\n" +
                           "                   currentcyckle=\"2\"/>\n" +
                           "        <transend idref=\"ID"+trans_ID+"\"/>\n" +
                           "        <placeend idref=\""+match_ID+"\"/>\n";


                   ID_counter+=10;


                   ID_counter+=10;
                   template+=
                           "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                   "          <posattr x=\""+(56+(i*50))+"\"\n" +
                                   "                   y=\""+(410+(i*50))+"+.000000\"/>\n" +
                                   "          <fillattr colour=\"White\"\n" +
                                   "                    pattern=\"Solid\"\n" +
                                   "                    filled=\"false\"/>\n" +
                                   "          <lineattr colour=\"Black\"\n" +
                                   "                    thick=\"0\"\n" +
                                   "                    type=\"Solid\"/>\n" +
                                   "          <textattr colour=\"Black\"\n" +
                                   "                    bold=\"false\"/>\n" +
                                   "          <text tool=\"CPN Tools\"\n" +
                                   "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                                   "        </annot>\n" +
                                   "      </arc>";


                   ///////////////////////////////////////////////////////////// DENEMEE


                   String matching_ID = "";
                   for (int k = 0; k < planns.getPlans().size(); k++) {

                       System.out.println("k"+k);

                       if (planns.getPlans().get(i).getTerm(0).toString().substring(0,planns.getPlans().get(i).getTerm(0).toString().indexOf('[')).equals(myTrans[k].plan_root_id)){

                           System.out.println("amanin z "+k+" "+planns.getPlans().get(i).getTerm(0).toString()+" "+myTrans[k].trans_appearing_name+" "+myTrans[k].plan_root_id);
                           matching_ID=myTrans[k].trans_long_id;
                       }
                   }



                   ID_counter+=10;
                   template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                           "           orientation=\"PtoT\"\n" +
                           "           order=\"1\">\n" +
                           "        <posattr x=\"0.000000\"\n" +
                           "                 y=\"0.000000\"/>\n" +
                           "        <fillattr colour=\"White\"\n" +
                           "                  pattern=\"\"\n" +
                           "                  filled=\"false\"/>\n" +
                           "        <lineattr colour=\"Black\"\n" +
                           "                  thick=\"1\"\n" +
                           "                  type=\"Solid\"/>\n" +
                           "        <textattr colour=\"Black\"\n" +
                           "                  bold=\"false\"/>\n" +
                           "        <arrowattr headsize=\"1.200000\"\n" +
                           "                   currentcyckle=\"2\"/>\n" +
                           "        <transend idref=\""+matching_ID+"\"/>\n" +
                           "        <placeend idref=\"ID"+place_id+"\"/>\n";



                   ID_counter+=10;
                   template+=
                           "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                   "          <posattr x=\"-"+(126+(i*50))+".000000\"\n" +
                                   "                   y=\"-"+(410+(i*50))+".000000\"/>\n" +
                                   "          <fillattr colour=\"White\"\n" +
                                   "                    pattern=\"Solid\"\n" +
                                   "                    filled=\"false\"/>\n" +
                                   "          <lineattr colour=\"Black\"\n" +
                                   "                    thick=\"0\"\n" +
                                   "                    type=\"Solid\"/>\n" +
                                   "          <textattr colour=\"Black\"\n" +
                                   "                    bold=\"false\"/>\n" +
                                   "          <text tool=\"CPN Tools\"\n" +
                                   "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                                   "        </annot>\n" +
                                   "      </arc>";

              //     System.out.println("rrr"+matching_ID);


////////////////////////////////////////////
               } /// end of else

               }




        }
        ///////////////////////////////////////////////////////////////////////////////////////

        for (int i = 0; i <myPH.length ; i++) {

            String trans_matching_ID = "";
            for (int k = 0; k < myTrans.length; k++) {

                System.out.println("k"+k);

                if (!myPH[k].in_or_out.equals("done") && myPH[i].plan_original_id.equals(myTrans[k].plan_root_id)){

                    System.out.println("simdi z "+myPH[i].plan_appearing_name+" "+myTrans[k].trans_long_id);
                    trans_matching_ID=myTrans[k].trans_long_id;


                    ID_counter+=10;
                    template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                            "           orientation=\"PtoT\"\n" +
                            "           order=\"1\">\n" +
                            "        <posattr x=\"0.000000\"\n" +
                            "                 y=\"0.000000\"/>\n" +
                            "        <fillattr colour=\"White\"\n" +
                            "                  pattern=\"\"\n" +
                            "                  filled=\"false\"/>\n" +
                            "        <lineattr colour=\"Black\"\n" +
                            "                  thick=\"1\"\n" +
                            "                  type=\"Solid\"/>\n" +
                            "        <textattr colour=\"Black\"\n" +
                            "                  bold=\"false\"/>\n" +
                            "        <arrowattr headsize=\"1.200000\"\n" +
                            "                   currentcyckle=\"2\"/>\n" +
                            "        <transend idref=\""+trans_matching_ID+"\"/>\n" +
                            "        <placeend idref=\""+myPH[i].plan_long_id+"\"/>\n";



                    ID_counter+=10;
                    template+=
                            "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                    "          <posattr x=\"-"+(126+(i*50))+"\"\n" +
                                    "                   y=\"-"+(310+(i*50))+".000000\"/>\n" +
                                    "          <fillattr colour=\"White\"\n" +
                                    "                    pattern=\"Solid\"\n" +
                                    "                    filled=\"false\"/>\n" +
                                    "          <lineattr colour=\"Black\"\n" +
                                    "                    thick=\"0\"\n" +
                                    "                    type=\"Solid\"/>\n" +
                                    "          <textattr colour=\"Black\"\n" +
                                    "                    bold=\"false\"/>\n" +
                                    "          <text tool=\"CPN Tools\"\n" +
                                    "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                                    "        </annot>\n" +
                                    "      </arc>";


                }




            }
            }




        ArrayList<String> printed_places = new ArrayList<>();
        var press=true;
        for (int i = 0; i < MiDPH.size(); i++) {

            for (int j = 0; j <printed_places.size() ; j++) {

                if ((printed_places.get(j).equals(MiDPH.get(i).plan_appearing_name))){
                    System.out.println("debug "+j+" "+printed_places.get(j)+" dbg"+MiDPH.get(i).plan_appearing_name);
                     press=false;
                }
            }

            if (press){
            ID_counter+=10;

            template+=" <place id=\""+MiDPH.get(i).plan_long_id+"\">\n" +
                    "        <posattr x=\"-"+(1962)+".000000\"\n" +
                    "                 y=\""+(-611-(60*i))+".000000\"/>\n" +
                    "        <fillattr colour=\"White\"\n" +
                    "                  pattern=\"\"\n" +
                    "                  filled=\"false\"/>\n" +
                    "        <lineattr colour=\"Black\"\n" +
                    "                  thick=\"1\"\n" +
                    "                  type=\"Solid\"/>\n" +
                    "        <textattr colour=\"Black\"\n" +
                    "                  bold=\"false\"/>\n" +
                    "        <text>"+"Root"+MiDPH.get(i).plan_appearing_name+"</text>\n" +
                    "        <ellipse w=\"132.000000\"\n" +
                    "                 h=\"56.000000\"/>\n" +
                    "        <token x=\"-10.000000\"\n" +
                    "               y=\"0.000000\"/>\n" +
                    "        <marking x=\"0.000000\"\n" +
                    "                 y=\"0.000000\"\n" +
                    "                 hidden=\"false\">\n" +
                    "          <snap snap_id=\"0\"\n" +
                    "                anchor.horizontal=\"0\"\n" +
                    "                anchor.vertical=\"0\"/>\n" +
                    "        </marking>\n" +
                    "        <type id=\"ID"+(ID_assign-ID_counter)*116+"\">\n" +
                    "          <posattr x=\""+(-1962)+".500000\"\n" +
                    "                   y=\""+(-611-(60*i))+".000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\">Beliefs</text>\n" +
                    "        </type>\n" +
                    "        <initmark id=\"ID"+((ID_assign-ID_counter)*117)+"\">\n" +
                    "          <posattr x=\"273.000000\"\n" +
                    "                   y=\"71.000000\"/>\n" +
                    "          <fillattr colour=\"White\"\n" +
                    "                    pattern=\"Solid\"\n" +
                    "                    filled=\"false\"/>\n" +
                    "          <lineattr colour=\"Black\"\n" +
                    "                    thick=\"0\"\n" +
                    "                    type=\"Solid\"/>\n" +
                    "          <textattr colour=\"Black\"\n" +
                    "                    bold=\"false\"/>\n" +
                    "          <text tool=\"CPN Tools\"\n" +
                    "                version=\"4.0.1\"/>\n" +
                    "        </initmark>\n" +
                    "      </place>";

            ////////////////////////////////////////////////////////////// ROOT transition ///////////////////////////////////


                ID_counter+=10;


                var trans_ID = ID_counter+ID_assign;

                template+=" <trans id=\"ID"+(trans_ID)+"\"\n" +
                        "             explicit=\"false\">\n" +
                        "        <posattr x=\"-112.000000\"\n" +
                        "                 y=\""+(-50*i)+".000000\"/>\n" +
                        "        <fillattr colour=\"White\"\n" +
                        "                  pattern=\"\"\n" +
                        "                  filled=\"false\"/>\n" +
                        "        <lineattr colour=\"Black\"\n" +
                        "                  thick=\"1\"\n" +
                        "                  type=\"solid\"/>\n" +
                        "        <textattr colour=\"Black\"\n" +
                        "                  bold=\"false\"/>\n" +
                        "        <text>Root"+MiDPH.get(i).plan_appearing_name+"</text>\n" +
                        "        <box w=\"210.000000\"\n" +
                        "             h=\"45.000000\"/>\n" +
                        "        <binding x=\"7.200000\"\n" +
                        "                 y=\"-3.000000\"/>\n";



                template+=
                        "        <cond id=\"ID"+(((ID_counter+ID_assign))*100)+"\">\n" +
                                "          <posattr x=\"-581.000000\"\n" +
                                "                   y=\"76.000000\"/>\n" +
                                "          <fillattr colour=\"White\"\n" +
                                "                    pattern=\"Solid\"\n" +
                                "                    filled=\"false\"/>\n" +
                                "          <lineattr colour=\"Black\"\n" +
                                "                    thick=\"0\"\n" +
                                "                    type=\"Solid\"/>\n" +
                                "          <textattr colour=\"Black\"\n" +
                                "                    bold=\"false\"/>\n" +
                                "          <text tool=\"CPN Tools\"\n" +
                                "                version=\"4.0.1\"/>\n" +
                                "        </cond>\n";

                ID_counter+=1;
                template+=
                        "        <time id=\"ID"+(((ID_counter+ID_assign))*100)+"\">\n" +
                                "          <posattr x=\"-581.500000\"\n" +
                                "                   y=\"78.000000\"/>\n" +
                                "          <fillattr colour=\"White\"\n" +
                                "                    pattern=\"Solid\"\n" +
                                "                    filled=\"false\"/>\n" +
                                "          <lineattr colour=\"Black\"\n" +
                                "                    thick=\"0\"\n" +
                                "                    type=\"Solid\"/>\n" +
                                "          <textattr colour=\"Black\"\n" +
                                "                    bold=\"false\"/>\n" +
                                "          <text tool=\"CPN Tools\"\n" +
                                "                version=\"4.0.1\"/>\n" +
                                "        </time>\n";
                ID_counter+=1;
                template+=  "        <code id=\"ID"+((ID_assign-ID_counter)*101)+"\">\n" +
                        "          <posattr x=\"-566.500000\"\n" +
                        "                   y=\"73.000000\"/>\n" +
                        "          <fillattr colour=\"White\"\n" +
                        "                    pattern=\"Solid\"\n" +
                        "                    filled=\"false\"/>\n" +
                        "          <lineattr colour=\"Black\"\n" +
                        "                    thick=\"0\"\n" +
                        "                    type=\"Solid\"/>\n" +
                        "          <textattr colour=\"Black\"\n" +
                        "                    bold=\"false\"/>\n" +
                        "          <text tool=\"CPN Tools\"\n" +
                        "                version=\"4.0.1\"/>\n" +
                        "        </code>\n";

                ID_counter+=1;
                template+=
                        "        <priority id=\"ID"+((ID_assign-ID_counter)*102)+"\">\n" +
                                "          <posattr x=\"-560.000000\"\n" +
                                "                   y=\"71.000000\"/>\n" +
                                "          <fillattr colour=\"White\"\n" +
                                "                    pattern=\"Solid\"\n" +
                                "                    filled=\"false\"/>\n" +
                                "          <lineattr colour=\"Black\"\n" +
                                "                    thick=\"0\"\n" +
                                "                    type=\"Solid\"/>\n" +
                                "          <textattr colour=\"Black\"\n" +
                                "                    bold=\"false\"/>\n" +
                                "          <text tool=\"CPN Tools\"\n" +
                                "                version=\"4.0.1\"/>\n" +
                                "        </priority>\n" +
                                "      </trans>";

                MiDTrans.add(new TransHoldings("ID"+trans_ID,"Root"+MiDPH.get(i).plan_appearing_name,
                        "",""));

ID_counter+=10;

                template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                        "           orientation=\"PtoT\"\n" +
                        "           order=\"1\">\n" +
                        "        <posattr x=\"0.000000\"\n" +
                        "                 y=\"0.000000\"/>\n" +
                        "        <fillattr colour=\"White\"\n" +
                        "                  pattern=\"\"\n" +
                        "                  filled=\"false\"/>\n" +
                        "        <lineattr colour=\"Black\"\n" +
                        "                  thick=\"1\"\n" +
                        "                  type=\"Solid\"/>\n" +
                        "        <textattr colour=\"Black\"\n" +
                        "                  bold=\"false\"/>\n" +
                        "        <arrowattr headsize=\"1.200000\"\n" +
                        "                   currentcyckle=\"2\"/>\n" +
                        "        <transend idref=\"ID"+trans_ID+"\"/>\n" +
                        "        <placeend idref=\""+MiDPH.get(i).plan_long_id+"\"/>\n";




                ID_counter+=10;
                template+=
                        "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                "          <posattr x=\"0.000000\"\n" +
                                "                   y=\""+(0)+".000000\"/>\n" +
                                "          <fillattr colour=\"White\"\n" +
                                "                    pattern=\"Solid\"\n" +
                                "                    filled=\"false\"/>\n" +
                                "          <lineattr colour=\"Black\"\n" +
                                "                    thick=\"0\"\n" +
                                "                    type=\"Solid\"/>\n" +
                                "          <textattr colour=\"Black\"\n" +
                                "                    bold=\"false\"/>\n" +
                                "          <text tool=\"CPN Tools\"\n" +
                                "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                                "        </annot>\n" +
                                "      </arc>";







            }

                printed_places.add(MiDPH.get(i).plan_appearing_name);





                press=true;




        }

////////////////////////////////////////////// ROOT TO SUB ARCS - CONDITIONALS ////////////////////////////
        for (int i = 0; i < myPH.length; i++) {

            for (int j = 0; j < MiDTrans.size(); j++) {


          //    System.out.println(MiDTrans.get(j).trans_appearing_name+" KKK "+ myPH[i].plan_appearing_name.substring(0,myPH[i].plan_appearing_name.length()-1) + " " + myPH[i].plan_original_id);


                if (MiDTrans.get(j).trans_appearing_name.contains(myPH[i].plan_appearing_name.substring(0,myPH[i].plan_appearing_name.length()-1))){






                    ID_counter+=10;

                    template+="<arc id=\"ID"+ID_counter+ID_assign+"\"\n" +
                            "           orientation=\"TtoP\"\n" +
                            "           order=\"1\">\n" +
                            "        <posattr x=\"0.000000\"\n" +
                            "                 y=\"0.000000\"/>\n" +
                            "        <fillattr colour=\"White\"\n" +
                            "                  pattern=\"\"\n" +
                            "                  filled=\"false\"/>\n" +
                            "        <lineattr colour=\"Black\"\n" +
                            "                  thick=\"1\"\n" +
                            "                  type=\"Solid\"/>\n" +
                            "        <textattr colour=\"Black\"\n" +
                            "                  bold=\"false\"/>\n" +
                            "        <arrowattr headsize=\"1.200000\"\n" +
                            "                   currentcyckle=\"2\"/>\n" +
                            "        <transend idref=\""+MiDTrans.get(j).trans_long_id+"\"/>\n" +
                            "        <placeend idref=\""+myPH[i].plan_long_id+"\"/>\n";





                    ID_counter+=10;
                    String mytext="";
                    System.out.println("DETECTED"+myPH[i].plan_appearing_name);
                    if (myPH[i].plan_appearing_name.equals("samplecolorF")){
                        System.out.println("DDDD");
                        mytext="if  CD = true then 1` (BF,D,CD,SES,CV) else empty";
                    }
                    else{
                        mytext=ConditionParser(myPH[i].plan_condition);
                    }



                    template+=
                            "        <annot id=\"ID"+(ID_assign-ID_counter)+"\">\n" +
                                    "          <posattr x=\""+(0)+"\"\n" +
                                    "                   y=\""+(0)+"+.000000\"/>\n" +
                                    "          <fillattr colour=\"White\"\n" +
                                    "                    pattern=\"Solid\"\n" +
                                    "                    filled=\"false\"/>\n" +
                                    "          <lineattr colour=\"Black\"\n" +
                                    "                    thick=\"0\"\n" +
                                    "                    type=\"Solid\"/>\n" +
                                    "          <textattr colour=\"Black\"\n" +
                                    "                    bold=\"false\"/>\n" +
                                    "          <text tool=\"CPN Tools\"\n" +
                                    "                version=\"4.0.1\">"+mytext+"</text>\n" +  //33`(BF,D,CD,SES,CV)
                                    "        </annot>\n" +
                                    "      </arc>";


                }

                }
            }



        for (int i = 0; i < MiDPH.size() ; i++) {  /// BU KESINNN

            for (int j = 0; j < myTrans.length; j++) {

           /*     System.out.println("Place "+MiDPH.get(i).plan_appearing_name+" JJJ Trans="+myTrans[j].trans_appearing_name+ "LLL"+
                        MiDPH.get(i).in_or_out
                    );
            */

     /*     System.out.println("BEFORE MATCHHHHHHH"+" "+
                  MiDPH.get(i).plan_appearing_name.
                          replaceAll("[A-Z]", "").substring(1)+" LLL "+ myTrans[j].trans_appearing_name.substring(myTrans[j].trans_appearing_name.indexOf("To")+2).
                  replaceAll("[A-Z]", ""));*/


                if  ((myTrans[j].trans_appearing_name.substring(myTrans[j].trans_appearing_name.indexOf("To")+2).
                        replaceAll("[A-Z]", "")).contains(MiDPH.get(i).plan_appearing_name.
                        replaceAll("[A-Z]", "").substring(1)) && !(myTrans[j].travelled.equals("travelled"))) {

                   myTrans[j].travelled="travelled";

     //           System.out.println("MATCHHHHHHH"+" "+myTrans[j].trans_appearing_name+" "+MiDPH.get(i).plan_appearing_name);

                    ID_counter += 10;

                    template += "<arc id=\"ID" + ID_counter + ID_assign + "\"\n" +
                            "           orientation=\"TtoP\"\n" +
                            "           order=\"1\">\n" +
                            "        <posattr x=\"0.000000\"\n" +
                            "                 y=\"0.000000\"/>\n" +
                            "        <fillattr colour=\"White\"\n" +
                            "                  pattern=\"\"\n" +
                            "                  filled=\"false\"/>\n" +
                            "        <lineattr colour=\"Black\"\n" +
                            "                  thick=\"1\"\n" +
                            "                  type=\"Solid\"/>\n" +
                            "        <textattr colour=\"Black\"\n" +
                            "                  bold=\"false\"/>\n" +
                            "        <arrowattr headsize=\"1.200000\"\n" +
                            "                   currentcyckle=\"2\"/>\n" +
                            "        <transend idref=\""+myTrans[j].trans_long_id+"\"/>\n" +
                            "        <placeend idref=\""+ MiDPH.get(i).plan_long_id +"\"/>\n";
                    ID_counter += 10;
                    template += "        <annot id=\"ID" + (ID_assign - ID_counter) + "\">\n" +
                            "          <posattr x=\"-" + (1962) + ".000000\"\n" +
                            "                   y=\"" + (-611-(60*i)) + ".000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                            "        </annot>\n" +
                            "      </arc>\n";



                }

                else if (myTrans[j].trans_appearing_name.equals("status0TostatusBack") && !(myTrans[j].travelled.equals("travelled"))) {

                    myTrans[j].travelled="travelled";

                    ID_counter += 10;

                    template += "<arc id=\"ID" + ID_counter + ID_assign + "\"\n" +
                            "           orientation=\"TtoP\"\n" +
                            "           order=\"1\">\n" +
                            "        <posattr x=\"0.000000\"\n" +
                            "                 y=\"0.000000\"/>\n" +
                            "        <fillattr colour=\"White\"\n" +
                            "                  pattern=\"\"\n" +
                            "                  filled=\"false\"/>\n" +
                            "        <lineattr colour=\"Black\"\n" +
                            "                  thick=\"1\"\n" +
                            "                  type=\"Solid\"/>\n" +
                            "        <textattr colour=\"Black\"\n" +
                            "                  bold=\"false\"/>\n" +
                            "        <arrowattr headsize=\"1.200000\"\n" +
                            "                   currentcyckle=\"2\"/>\n" +
                            "        <transend idref=\""+myTrans[j].trans_long_id+"\"/>\n" +
                            "        <placeend idref=\"ID"+ init_goal_p_id +"\"/>\n";
                    ID_counter += 10;
                    template += "        <annot id=\"ID" + (ID_assign - ID_counter) + "\">\n" +
                            "          <posattr x=\"" + (292 + (i * 20)) + ".000000\"\n" +
                            "                   y=\"" + (-75 * i) + ".000000\"/>\n" +
                            "          <fillattr colour=\"White\"\n" +
                            "                    pattern=\"Solid\"\n" +
                            "                    filled=\"false\"/>\n" +
                            "          <lineattr colour=\"Black\"\n" +
                            "                    thick=\"0\"\n" +
                            "                    type=\"Solid\"/>\n" +
                            "          <textattr colour=\"Black\"\n" +
                            "                    bold=\"false\"/>\n" +
                            "          <text tool=\"CPN Tools\"\n" +
                            "                version=\"4.0.1\">(BF,D,CD,SES,CV)</text>\n" +
                            "        </annot>\n" +
                            "      </arc>\n";


                    for (int k = 0; k <2 ; k++) {  //init_goal_t_id

                       var p_id= places_and_ids.get("status"+k);
                        //System.out.println("last stand "+p_id);


                        ID_counter += 10;

                        template += "<arc id=\"ID" + ID_counter + ID_assign + "\"\n" +
                                "           orientation=\"TtoP\"\n" +
                                "           order=\"1\">\n" +
                                "        <posattr x=\"0.000000\"\n" +
                                "                 y=\"0.000000\"/>\n" +
                                "        <fillattr colour=\"White\"\n" +
                                "                  pattern=\"\"\n" +
                                "                  filled=\"false\"/>\n" +
                                "        <lineattr colour=\"Black\"\n" +
                                "                  thick=\"1\"\n" +
                                "                  type=\"Solid\"/>\n" +
                                "        <textattr colour=\"Black\"\n" +
                                "                  bold=\"false\"/>\n" +
                                "        <arrowattr headsize=\"1.200000\"\n" +
                                "                   currentcyckle=\"2\"/>\n" +
                                "        <transend idref=\"ID"+first_goal_t+"\"/>\n" +
                                "        <placeend idref=\""+ p_id +"\"/>\n";
                        ID_counter += 10;
                        String text = "";
                            if (k ==0)
                                text="if D=false then 1`(BF,D,CD,SES,CV) else empty";
                            else if (k==1)
                                text="if D=true then 1`(BF,D,CD,SES,CV) else empty";


                        template += "        <annot id=\"ID" + (ID_assign - ID_counter) + "\">\n" +
                                "          <posattr x=\"" + (292 + (i * 20)) + ".000000\"\n" +
                                "                   y=\"" + (-50 * i) + ".000000\"/>\n" +
                                "          <fillattr colour=\"White\"\n" +
                                "                    pattern=\"Solid\"\n" +
                                "                    filled=\"false\"/>\n" +
                                "          <lineattr colour=\"Black\"\n" +
                                "                    thick=\"0\"\n" +
                                "                    type=\"Solid\"/>\n" +
                                "          <textattr colour=\"Black\"\n" +
                                "                    bold=\"false\"/>\n" +
                                "          <text tool=\"CPN Tools\"\n" +
                                "                version=\"4.0.1\">"+text+"</text>\n" +
                                "        </annot>\n" +
                                "      </arc>\n";


                    }


                }





                }
            }











        // END HERE


//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        template+="</page>\n" +
                "    <instances>\n" +
                "      <instance id=\"ID2149\"\n" +
                "                page=\"ID6\"/>\n" +
                "    </instances>\n" +
                "    <options>\n" +
                "      <option name=\"realtimestamp\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"fair_be\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"global_fairness\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"outputdirectory\">\n" +
                "        <value>\n" +
                "          <text>&lt;same as model&gt;</text>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10007.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10005.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10001.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10006.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10011.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10003.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10004.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10008.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10002.enable\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10007.options.discover\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"extensions.10011.options.disable\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repavg\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repciavg\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repcount\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repfirstval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"replastval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repmax\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repmin\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repssquare\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repssqdev\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repstddev\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repsum\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"repvariance\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"avg\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"ciavg\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"count\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"firstval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"lastval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"max\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"min\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"ssquare\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"ssqdev\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"stddev\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"sum\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"variance\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"firstupdate\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"interval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"lastupdate\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedavg\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedciavg\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedcount\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedfirstval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedlastval\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedmax\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedmin\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedssquare\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedssqdev\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedstddev\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedsum\">\n" +
                "        <value>\n" +
                "          <boolean>true</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "      <option name=\"untimedvariance\">\n" +
                "        <value>\n" +
                "          <boolean>false</boolean>\n" +
                "        </value>\n" +
                "      </option>\n" +
                "    </options>\n" +
                "    <binders>\n" +
                "      <cpnbinder id=\"ID2222\"\n" +
                "                 x=\"212\"\n" +
                "                 y=\"-127\"\n" +
                "                 width=\"1908\"\n" +
                "                 height=\"997\">\n" +
                "        <sheets>\n" +
                "          <cpnsheet id=\"ID2215\"\n" +
                "                    panx=\"4.790933\"\n" +
                "                    pany=\"-260.809073\"\n" +
                "                    zoom=\"0.805664\"\n" +
                "                    instance=\"ID2149\">\n" +
                "            <zorder>\n" +
                "              <position value=\"0\"/>\n" +
                "            </zorder>\n" +
                "          </cpnsheet>\n" +
                "        </sheets>\n" +
                "        <zorder>\n" +
                "          <position value=\"0\"/>\n" +
                "        </zorder>\n" +
                "      </cpnbinder>\n" +
                "    </binders>\n" +
                "    <monitorblock name=\"Monitors\"/>\n" +
                "    <IndexNode expanded=\"true\">\n" +
                "      <IndexNode expanded=\"false\"/>\n" +
                "      <IndexNode expanded=\"false\"/>\n" +
                "      <IndexNode expanded=\"false\">\n" +
                "        <IndexNode expanded=\"false\"/>\n" +
                "        <IndexNode expanded=\"false\"/>\n" +
                "        <IndexNode expanded=\"false\"/>\n" +
                "        <IndexNode expanded=\"false\"/>\n" +
                "        <IndexNode expanded=\"false\">\n" +
                "          <IndexNode expanded=\"false\">\n" +
                "            <IndexNode expanded=\"false\">\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "            </IndexNode>\n" +
                "            <IndexNode expanded=\"false\">\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "              <IndexNode expanded=\"false\"/>\n" +
                "            </IndexNode>\n" +
                "          </IndexNode>\n" +
                "          <IndexNode expanded=\"false\">\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "            <IndexNode expanded=\"false\"/>\n" +
                "          </IndexNode>\n" +
                "        </IndexNode>\n" +
                "        <IndexNode expanded=\"false\">\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "        </IndexNode>\n" +
                "        <IndexNode expanded=\"false\">\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "        </IndexNode>\n" +
                "        <IndexNode expanded=\"false\">\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "        </IndexNode>\n" +
                "      </IndexNode>\n" +
                "      <IndexNode expanded=\"false\">\n" +
                "        <IndexNode expanded=\"false\"/>\n" +
                "      </IndexNode>\n" +
                "      <IndexNode expanded=\"true\">\n" +
                "        <IndexNode expanded=\"false\">\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "        </IndexNode>\n" +
                "        <IndexNode expanded=\"true\">\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "          <IndexNode expanded=\"true\"/>\n" +
                "          <IndexNode expanded=\"false\"/>\n" +
                "        </IndexNode>\n" +
                "      </IndexNode>\n" +
                "      <IndexNode expanded=\"false\"/>\n" +
                "      <IndexNode expanded=\"true\"/>\n" +
                "    </IndexNode>\n" +
                "  </cpnet>\n" +
                "</workspaceElements>";



        System.out.println(template);

        Files.writeString(Path.of("C:\\Users\\Burak\\OneDrive - Universiteit Antwerpen\\Bureaublad\\Activities\\Publications\\Petri-Nets-Analysis\\deneme.cpn"), template);




//        for (int i = 0; i < MiDPH.size(); i++) {
//
//            System.out.println(MiDPH.get(i).plan_original_id+" "+MiDPH.get(i).);
//        }




//        for (var plan: ag.getPL()) {
//            int plan_count=1;
//            System.out.println("---------------------------------------------------------------");
//            while (plan.getTerm(plan_count)!=null){
//
//            System.out.println(plan.getTerm(plan_count));
//            plan_count+=1;
//            }
//            plan_count=1;
//        }
//
//        for (int i = 0; i < myPH.length ; i++) {
//            System.out.println(myPH[i].toString());
//        }



        System.exit(0);
    }
}