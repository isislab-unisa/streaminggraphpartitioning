plotallordoff <- function(file, fileName, step, start1,start2,start3, metisgr) {
  for (i in c(0:6)) {
    off <- 6
    soff <- step - off
    stoff <- 3
    sstof <- 4
		hNames <- as.character(file$HeuristicName[sstof:soff])
		bfsStartIndex <- (i*step)  + stoff
		bfsEndIndex <- (i*step) + step - off
		bfsArr <- as.numeric(as.character(file[bfsStartIndex:bfsEndIndex, c("CuttedEdgesRatio")]))
		dfsStartIndex <- (i*step) + start2  + stoff
		dfsEndIndex <- (i*step) + (start2+step-1 - off)
		dfsArr <- as.numeric(as.character(file[dfsStartIndex:dfsEndIndex, c("CuttedEdgesRatio")]))
		rndStartIndex <- (i*step) +start3  + stoff
		rndEndIndex <- (i*step) + (start3+step-1 - off)
		rndArr <- as.numeric(as.character(file[rndStartIndex:rndEndIndex, c("CuttedEdgesRatio")]))
		cols <- rep(c("blue","red","green"),step)
		mainText <- paste(fileName, paste(" k=", 2^(i+1)))
		a <- vector()
		for (j in c(length(bfsArr):1)) {a <- append(c(bfsArr[j],dfsArr[j],rndArr[j]), a) }
		maxX <- c(0,1)
		tiff(paste(paste(mainText,"allbp",sep="-"),"tiff",sep="."), width = 1200, height = 600, units = 'px')
		#jpeg(paste(paste(mainText,"allbp",sep="-"),"jpeg",sep="."), width = 1366, height = 768, units = 'px')
		mp <- barplot(t(matrix(a,ncol=3,byrow = TRUE, dimnames = list(hNames, c("BFS","DFS","RANDOM")))),main =mainText, col=cols, beside = TRUE, legend.text = TRUE,args.legend = list(x = "topright"), ylim=maxX, las=2,space=c(0,2), ylab="Edge Cut Ratio")
  ##		mtext(side = 3, at = mp, text = a, las=3)
		par(new=TRUE)
		metisInd <- retrievemetis(fileName, 2^i, metisgr)
		ymetis <- metisgr$CuttedEdgesRatio[metisInd]
		abline(h = ymetis)
		dev.off()
	}
}