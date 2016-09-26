## grafico 3
## x file
## n maximum heuristics
## 
whichwithk <- function(xData, n=10, step, start1, start2,start3, ret="" ) {
  totalMatrix <- matrix(0, ncol=7, nrow=step)
  for (i in c(0:6)) {
    
    stInd <- (i*step) +start1
    endInd <- stInd + step -1
    xavg1 <- matrix(data=as.numeric(as.character(xData$CuttedEdgesRatio[stInd:endInd])), nrow=step,ncol=1,byrow=TRUE)
     stInd <- (i*step) +start2
    endInd <- stInd + step -1
    xavg2 <- matrix(data=as.numeric(as.character(xData$CuttedEdgesRatio[stInd:endInd])), nrow=step,ncol=1,byrow=TRUE)
    
    stInd <- (i*step) + start3
    endInd <- stInd + step -1
    xavg3 <- matrix(data=as.numeric(as.character(xData$CuttedEdgesRatio[stInd:endInd])), nrow=step,ncol=1,byrow=TRUE)
    
    avgmtx <- matrix(0,nrow=step, ncol=3, byrow=TRUE)
    avgmtx[,1] <- xavg1
    avgmtx[,2] <- xavg2
    avgmtx[,3] <- xavg3
    totavg <- rowMeans(avgmtx)
    
    totalMatrix[,i+1] <- totavg
  }
  vvvv <- step
  A <- matrix(data = rowMeans(totalMatrix), nrow=step, ncol=1, byrow = TRUE, dimnames = list(xData$HeuristicName[1:vvvv],c("val")))
   indexes = which(A <= sort(A,partial=n)[n])
  if (ret == "i") {
    return(indexes)
  } else if (ret == "m") {
    return(totalMatrix)
  }
  return(list(totalMatrix,indexes))
  
}
