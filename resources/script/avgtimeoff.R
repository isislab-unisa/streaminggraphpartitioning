avgtimeoff <- function(x,step, start1,start2,start3) {
  off <- 6
  startoff <- 3
  totalMatrix <- matrix(0, ncol=7, nrow=step - off -startoff)
  mmm <- matrix(0, ncol=3, nrow=step- off - startoff, byrow=TRUE)
  for (i in c(0:6)) {
    
    stInd <- (i*step) +start1 + startoff
    endInd <- (i*step) + step- off
    xavg <- as.numeric(as.character(x$AvgTime[stInd:endInd]))

    stInd <- (i*step) +start2 +startoff
    endInd <- (i*step) + (start2+step-1)- off
    xavg2 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    
    stInd <- (i*step) + start3 +startoff
    endInd <- (i*step) + (start3+step-1)- off
    xavg3 <- as.numeric(as.character(x$AvgTime[stInd:endInd]))
    mmm[,1] <- xavg
    mmm[,2] <- xavg2
    mmm[,3] <- xavg3
    totavg <- rowMeans(mmm)
    totalMatrix[,i+1] <- totavg
  }
  soff <- step - off
  stoff <- 1 + startoff
  rownames(totalMatrix)<-x$HeuristicName[stoff:soff]
  
  return(totalMatrix)
}