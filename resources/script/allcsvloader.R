toRemoveString <- nchar(".graph.res")
# step <- 8
# start1 <- 1
# start2 <- 58
# start3 <- 115
# n <- 5 
  # step <- 28
  # start1 <- 1
  # start2 <- 198
  # start3 <- 395
  # n <- 4
colz <- c("green","blue","red","black","pink")
allscripts <- function() {
  path <- paste(getwd(),"/script/", sep="")
  actwd <- getwd()
  setwd(path)
  for (f in list.files(pattern = "*.R")) {
    source(f)
  }
  for (f in list.files(pattern = "*.rda")) {
    load(f)
  }
  setwd(actwd)
}

allcsvloader <- function() {
  path <- paste(getwd(),"/")
  
  temp = list.files(pattern="*.csv")
  list2env(
    lapply(setNames(temp, make.names(gsub("*.csv$", "", temp))), 
           read.csv,sep=" "), envir = .GlobalEnv)
}

plotallfiles <- function(files, step, start1,start2,start3, n = 5, metisfile) {
  for(file in files) {
    dataFile <- get(file)
    colz <- palette(rainbow(n))
    print(paste("plotting ", file, sep=":"))
    graphName <- substr(file, 0,nchar(file)-toRemoveString)
    # plottime(dataFile,graphName,colz,n, step, start1,start2,start3)
    #plottimeoff(dataFile,graphName,colz,n, step, start1,start2,start3)
     #plottimesocial(dataFile,graphName,colz,n, step, start1,start2,start3)
    #avgplotsocial(dataFile, graphName, step,start1, start2,start3, metisfile)
   avgplotoffset(dataFile, graphName, step,start1, start2,start3, metisfile)
  #  plotallord(dataFile, graphName, step,start1, start2,start3, metisfile)
     # plotallordsocial(dataFile, graphName, step,start1, start2,start3, metisfile)
    plotallordoff(dataFile, graphName, step,start1, start2,start3, metisfile)
  # plotedgecutsocial(dataFile,graphName,colz,n,step,start1,start2,start3)
 #   plotedgecutoff(dataFile,graphName,colz,n,step,start1,start2,start3)
    
    
  }
}

wholeavg <- function(files, type,step,start1,start2,start3, ssep=" ", qquote=FALSE, fname="") {
  hNames <- get(files[1])$HeuristicName[1:step-6]
  avgmtx <- matrix(0, nrow=step-6,ncol=length(files),byrow = TRUE, dimnames = list(hNames,files))
  avgmtxbfs <- matrix(0,nrow=step-6,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  avgmtxdfs <- matrix(0,nrow=step-6,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  avgmtxrnd <- matrix(0,nrow=step-6,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  i <- 0
  for (file in files) {
    print(paste("Analyzing graph: ", file))
    avgmtxbdr <- matrix(0,nrow=step, ncol=3,byrow = TRUE)
    i <- i+1
    dataFile <- get(file)
    avgmtxbdr[,1] <- getrowavg(dataFile, start1, step, type)
    avgmtxbdr[,2] <- getrowavg(dataFile, start2, step, type)
    avgmtxbdr[,3] <- getrowavg(dataFile, start3, step, type)
    avgmtxbfs[,i] <- avgmtxbdr[,1]
    avgmtxdfs[,i] <- avgmtxbdr[,2]
    avgmtxrnd[,i] <- avgmtxbdr[,3]
    avgmtx[,i] <- rowMeans(avgmtxbdr)
  }
  write.csv(avgmtxbfs,file=paste(fname,type,"bfsavg.csv",sep="-"), sep = ssep, quote=qquote)
  
  write.csv(avgmtxdfs,file=paste(fname,type,"dfsavg.csv",sep="-"), sep = ssep, quote=qquote)
  write.csv(avgmtxrnd,file=paste(fname,type,"rndavg.csv",sep="-"), sep = ssep, quote=qquote)
  write.csv(avgmtx,file = paste(fname,type,"totalavg.csv",sep="-"), sep = ssep, quote=qquote)
  singleAvg <- matrix(data = rowMeans(avgmtx), ncol=1,nrow=step, dimnames = list(hNames,type))
  write.csv(singleAvg, file=paste(type,"singleavg.csv",sep="-"), sep = ssep, quote=qquote)
  return(singleAvg)
}

wholeavgf2 <- function(files,step,start1,start2,start3, ssep=" ", qquote=FALSE, fname="") {
  hNames <- get(files[1])$HeuristicName[1:step]
  avgmtx <- matrix(0, nrow=step,ncol=length(files),byrow = TRUE, dimnames = list(hNames,files))
  avgmtxbfs <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  avgmtxdfs <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  avgmtxrnd <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  
  tavgmtx <- matrix(0, nrow=step,ncol=length(files),byrow = TRUE, dimnames = list(hNames,files))
  tavgmtxbfs <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  tavgmtxdfs <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  tavgmtxrnd <- matrix(0,nrow=step,ncol=length(files),byrow=TRUE, dimnames = list(hNames,files))
  head = c("Time-R","EdgeCut-R","Time-D","EdgeCut-D","Time-B","EdgeCut-B","Time-A","EdgeCut-A")
  generalMatrix <- matrix(0,nrow=length(hNames),ncol= length(head) ,byrow = FALSE, dimnames = list(hNames,head))

  i <- 0
  for (file in files) {
    print(paste("Analyzing graph: ", file))
    avgmtxbdr <- matrix(0,nrow=step, ncol=3,byrow = TRUE)
    tavgmtxbdr <- matrix(0,nrow=step, ncol=3,byrow = TRUE)
    i <- i+1
    dataFile <- get(file)
    avgmtxbdr[,1] <- getrowavg(dataFile, start1, step, "ec")
    avgmtxbdr[,2] <- getrowavg(dataFile, start2, step, "ec")
    avgmtxbdr[,3] <- getrowavg(dataFile, start3, step, "ec")
    avgmtxbfs[,i] <- avgmtxbdr[,1]
    avgmtxdfs[,i] <- avgmtxbdr[,2]
    avgmtxrnd[,i] <- avgmtxbdr[,3]
    avgmtx[,i] <- rowMeans(avgmtxbdr)
    
    tavgmtxbdr[,1] <- getrowavg(dataFile, start1, step, "t")
    tavgmtxbdr[,2] <- getrowavg(dataFile, start2, step, "t")
    tavgmtxbdr[,3] <- getrowavg(dataFile, start3, step, "t")
    tavgmtxbfs[,i] <- tavgmtxbdr[,1]
    tavgmtxdfs[,i] <- tavgmtxbdr[,2]
    tavgmtxrnd[,i] <- tavgmtxbdr[,3]
    tavgmtx[,i] <- rowMeans(tavgmtxbdr)
  }
  generalMatrix[,1] <- round(rowMeans(tavgmtxrnd),digits = 2)
  generalMatrix[,2] <- round(rowMeans(avgmtxrnd),digits = 3)
  generalMatrix[,3] <- round(rowMeans(tavgmtxdfs),digits = 2)
  generalMatrix[,4] <- round(rowMeans(avgmtxdfs),digits = 3)
  generalMatrix[,5] <- round(rowMeans(tavgmtxbfs),digits = 2)
  generalMatrix[,6] <- round(rowMeans(avgmtxbfs),digits = 3)
  generalMatrix[,7] <- round(rowMeans(tavgmtx),digits = 2)
  generalMatrix[,8] <- round(rowMeans(avgmtx),digits = 3)
  write.csv(generalMatrix, file="grafici/totalRes1.csv", sep = ssep, quote=qquote)
  View(generalMatrix)
  
  return(1)
}

getrowavg <- function(datafile, start, step, val ="ec") {
  avgmtx <- matrix(0, nrow=step, ncol=7, byrow=TRUE)
  for ( i in c(0:6)) {
    stInd <- (i*step) + start
    endInd <- stInd + step -1
    if (val == "ec") {
      avgmtx[,i+1] <- as.numeric(as.character(datafile$CuttedEdgesRatio[stInd:endInd]))
    } else if (val == "t") {
      avgmtx[,i+1] <- as.numeric(as.character(datafile$AvgTime[stInd:endInd]))
    }
  }
  toRetMatrix <- rowMeans(avgmtx)
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  return (toRetMatrix)
}

wholeavgsocial <- function(files) {
  n <- 5
  start1 <- 1
  start2 <- 58
  start3 <- 115
  step <- 8
  ssep <- "&"
  qquote <- FALSE
  fname <- "social"
  wholeavg(files,"t",step,start1,start2,start3,ssep, qquote,fname)
  wholeavg(files,"ec",step,start1,start2,start3,ssep, qquote,fname)
}

wholeavgnn <- function(files) {
  step <- 28
  start1 <- 1
  start2 <- 198
  start3 <- 395
  n <- 4
  ssep <- "&"
  qquote <- FALSE
  fname <- "fs-dataset"
  wholeavg(files,"t",step,start1,start2,start3,ssep, qquote,fname)
  wholeavg(files,"ec",step,start1,start2,start3,ssep, qquote,fname)
}