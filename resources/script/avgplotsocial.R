avgplotsocial <- function(file, fileName, step, start1,start2,start3, metisgr) {
	for (i in c(0:6)) {
		hNames <- as.character(file$HeuristicName[2:step])
		bfsStartIndex <- (i*step) +start1
		bfsEndIndex <- (i*step) + step
		bfsArr <- as.numeric(as.character(file[bfsStartIndex:bfsEndIndex, c("CuttedEdgesRatio")]))
		dfsStartIndex <- (i*step) + start2
		dfsEndIndex <- (i*step) + (start2+step-1)
		dfsArr <- as.numeric(as.character(file[dfsStartIndex:dfsEndIndex, c("CuttedEdgesRatio")]))
		rndStartIndex <- (i*step) +start3
		rndEndIndex <- (i*step) + (start3+step-1)
		rndArr <- as.numeric(as.character(file[rndStartIndex:rndEndIndex, c("CuttedEdgesRatio")]))
		mainText <- paste(fileName, paste(" k=", 2^(i+1)))
		a <- vector()
		for (j in c(length(bfsArr):2)) {a <- append(mean(c(bfsArr[j],dfsArr[j],rndArr[j])), a) }
		maxX <- c(0, 1)
		tiff(paste(paste(mainText,"avgbp",sep="-"),"tiff",sep="."), width = 1280, height = 600, units = 'px')
		#jpeg(paste(paste(mainText,"avgbp",sep="-"),"jpeg",sep="."), width = 1366, height = 768, units = 'px')
		barplot(t(matrix(a,ncol=1,byrow = TRUE,dimnames = list(hNames, c("AVG")))),main =mainText,beside = TRUE, legend.text = TRUE,args.legend = list(x = "topright"), ylim=maxX, las=2,space=c(0,2), ylab="Edge Cut Ratio")
		par(new = TRUE)
		metisInd <- retrievemetis(fileName, 2^i, metisgr)
		ymetis <- metisgr$CuttedEdgesRatio[metisInd]
		abline(h = ymetis)
		par(new = TRUE)
		abline(h = mean(bfsArr[1],dfsArr[1],rndArr[1]), col = "red")
		dev.off()
	}
}