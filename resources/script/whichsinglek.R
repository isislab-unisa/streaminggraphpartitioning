## grafico 3
## x file
## n maximum heuristics
## 
whichsinglek <- function(x, n=10, step, start1, start2,start3,i ) {
  totalMatrix <- matrix(0, ncol=7, nrow=step)
    
    stInd <- (i*step) +start1
    endInd <- stInd + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg1 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=TRUE, ncol=3))
    
    stInd <- (i*step) +start2
    endInd <- stInd + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg2 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=TRUE, ncol=3))
    
    stInd <- (i*step) + start3
    endInd <- stInd + step -1
    xavg <- as.numeric(as.character(x$CuttedEdgesRatio[stInd:endInd]))
    xmin <- as.numeric(as.character(x$MinCuttedEdgesRatio[stInd:endInd]))
    xmax <- as.numeric(as.character(x$MaxCuttedEdgesRatio[stInd:endInd]))
    avg3 <- rowMeans(matrix(data = c(xavg,xmin,xmax),byrow=TRUE, ncol=3))
    
    totavg <- rowMeans(matrix(data = c(avg1,avg2,avg3), byrow=TRUE, ncol=3))
    totalMatrix[,i+1] <- totavg
  
  A <- matrix(data = rowMeans(totalMatrix), nrow=step, ncol=1, byrow = TRUE, dimnames = list(x$HeuristicName[1:step],c("val")))
  indexes = which(A <= sort(A,partial=n)[n])
  return(list(A,indexes))
  
}
