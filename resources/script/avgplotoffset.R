avgplotoffset <- function(file, fileName, step, start1,start2,start3, metisgr) {
	for (i in c(0:6)) {
	  off <- 6
	  stoff <- 3
	  sstof <- 4
	  soff <- step - off
		hNames <- as.character(file$HeuristicName[sstof:soff])
		bfsStartIndex <- (i*step) +start1 + stoff
		bfsEndIndex <- (i*step) + step - off
		bfsArr <- as.numeric(as.character(file[bfsStartIndex:bfsEndIndex, c("CuttedEdgesRatio")]))
		dfsStartIndex <- (i*step) + start2 + stoff
		dfsEndIndex <- (i*step) + (start2+step-1-off)
		dfsArr <- as.numeric(as.character(file[dfsStartIndex:dfsEndIndex, c("CuttedEdgesRatio")]))
		rndStartIndex <- (i*step) +start3 + stoff
		rndEndIndex <- (i*step) + (start3+step-1-off)
		rndArr <- as.numeric(as.character(file[rndStartIndex:rndEndIndex, c("CuttedEdgesRatio")]))
		mainText <- paste(fileName, paste(" k=", 2^(i+1)))
		a <- vector()
		for (j in c(length(bfsArr):1)) {a <- append(mean(c(bfsArr[j],dfsArr[j],rndArr[j])), a) }
		maxX <- c(0, a[which.max(a)] + 0.1)
		tiff(paste(paste(mainText,"avgbp-off",sep="-"),"tiff",sep="."), width = 1200, height = 600, units = 'px')
		#jpeg(paste(paste(mainText,"avgbp",sep="-"),"jpeg",sep="."), width = 1366, height = 768, units = 'px')
		barplot(t(matrix(a,ncol=1,byrow = TRUE,dimnames = list(hNames, c("AVG")))),main =mainText,beside = TRUE, legend.text = TRUE,args.legend = list(x = "topright"), ylim=maxX, las=2,space=c(0,2), ylab="Edge Cut Ratio")
		par(new = TRUE)
		metisInd <- retrievemetis(fileName, 2^i, metisgr)
		ymetis <- metisgr$CuttedEdgesRatio[metisInd]
		abline(h = ymetis)
		dev.off()
	}
}