// Agent sortAgent in project productionLine

/* Initial beliefs and rules */




buildFree(false).
dropped(false).
colorDecided(false).
shredEndSent(false).
colorValue(0).
/* Initial goals */

!status.


/* Plans */


//+!init <-   //.wait(5000);


//+!status : true <-  .print("message sort").

+!status : dropped(false) <-  !status.
+!status : dropped(true) <-  .print("MESSAGE SORT - dropped"); !samplecolor. //!!checkReverse [uncomment this later];




//@sampleColor1 [atomic]	
+!samplecolor: colorDecided(CDS)  & CDS==false <- sampleColor;  ?colorValue(CV);  .print("---------Sampled Color Value =-------------", CV); !decidecolor(CV).

//@sampleColor2 [atomic]	
-!samplecolor <-!samplecolor. // .drop_all_intentions; 


//@decidecolor1 [atomic]
+!decidecolor(K):  K>2 & K<7 | K==1 <- .print("Sampled Color is ",K);.print("Color is being decided");sampleColorS;-+colorDecided(true); ?colorValue(U);!colorbucket(U). // [bandi durdurmak istersek ekle]; 

//@decidecolor2 [atomic]
+!decidecolor(K):  K==0 | K==2 <- .print(" No new Color ",K); !samplecolor.





//+!checkReverse: true <- ?count_time(M); .print("MMMMMMMMMMMM",M); New_time=M+1; -+count_time(New_time); !decideReverse(New_time). 


//+!decideReverse(Cc): Cc>25 | Cc==25 <- -+count_time(35); conveyorReverse; -+reverse_triggered(true); .print("REVERSE IT");!checkReverse.

//+!decideReverse(Cc): Cc<25 <- .print("NRA");!checkReverse.

//-!decideReverse <- !decideReverse.



+!colorbucket(F): F==5 | F==3 | F==4<- .print("Sending to Build Agent");!toBuild;+toBuild(true). //?redCount(GETMYRED); MYRED=GETMYRED+1; -+redCount(MYRED); [kolaylik acisindan pusha gonder] !toPush(F);+toPush(true)   

+!colorbucket(F): F\==5 & F\==3 & F\==2 & F\==4<- .print("Sending to Bucket and PushAgent ",F);!toPush(F);+toPush(true).

+!colorbucket(F): F==1 <- .print("Sending to 111111 Push ",F);!toPush(F);+toPush(true).

+!colorbucket(F): F== 2 <- -+colorDecided(false); !samplecolor.


//+dropped(M)[source(dropAgent)] : true
//   <- .print("Message from ",dropAgent,": ",M);
//      -msg(M);-+dropped(true);!status.
      
      
      
//+buildFree(M)[source(buildAgent)] : true
//   <- .print("**MMMMMMMMessage from ",buildAgent,": ",M);
//      -msg(M);.abolish(buildFree(_)); -+buildFree(M).
      

      
      
+!shredend: true <- .send(shredAgent,achieve,shredStartt).

+!shredstart: true <- .send(shredAgent,achieve,shredStopp).



+!toPush(Ph): Ph==1 <- goYellowPosition;  .send(pushAgent,achieve,push); .print(" Yellow - A message should be sent to Push Agent."); .send(dropAgent,achieve,loop); !shredstart; .print("A message should be sent to Push Agent OR LOW COLOR DETECTED."); -+colorDecided(false);  !samplecolor.
+!toPush(Ph): Ph==2 <- goBluePosition;    .send(pushAgent,achieve,push); .print(" Blue   - A message should be sent to Push Agent."); .send(dropAgent,achieve,loop); !shredstart; -+colorDecided(false); !samplecolor.      //.send([pushAgent],achieve,push). // go to samplecolor.
+!toPush(Ph): Ph==3 <- goGreenPosition;   .send(pushAgent,achieve,push); .print(" Green  - A message should be sent to Push Agent."); .send(dropAgent,achieve,loop); !shredstart; -+colorDecided(false); !samplecolor.
+!toPush(Ph): Ph==4 <- goYellowPosition;  .send(pushAgent,achieve,push); .print(" Yellow - A message should be sent to Push Agent."); .send(dropAgent,achieve,loop); !shredstart; -+colorDecided(false); !samplecolor.
+!toPush(Ph): Ph==5 <- goYellowPosition;  .send(pushAgent,achieve,push); .print(" Red - A message should be sent to Push Agent.");    .send(dropAgent,achieve,loop); !shredstart; -+colorDecided(false); !samplecolor.
+!toPush(Ph): Ph==6 <- goYellowPosition;  .send(pushAgent,achieve,push); .print(" Yellow/White - A message should be sent to Push Agent."); .send(dropAgent,achieve,loop); !shredstart; -+colorDecided(false);  !samplecolor.
+!toPush(Ph): Ph\==6 |Ph\==4 |Ph\==5 <- goYellowPosition;     .print("A message should be sent to Push Agent OR LOW COLOR DETECTED."); -+colorDecided(false);  !samplecolor.


+!toBuild: buildFree(true) <-  goRedPosition; .send(buildAgent,achieve,build); -+colorDecided(false); .print("A message should be sent to Build Agent."); !shredstart; !samplecolor.    //.send([buildAgent],achieve,build). // go to samplecolor.
+!toBuild: buildFree(false) <- goYellowPosition; .send(pushAgent,achieve,push); .print("Build is not free RED is discarded"); -+colorDecided(false); !shredstart;!samplecolor. 


