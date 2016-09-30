package it.isislab.streamingkway.kwaysgp.finaltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.ui.spriteManager.SpriteManager;

import it.isislab.streamingkway.graphloaders.AbstractGraphLoader;
import it.isislab.streamingkway.graphloaders.graphtraversingordering.GraphTraversingOrdering;
import it.isislab.streamingkway.graphpartitionator.GraphPartitionator;


public class GraphDrawer extends AbstractGraphLoader {

	ArrayList<Node> nodesTrav = new ArrayList<Node>();
	Map<Node, Integer> mapNode = new HashMap<>();

	public static void main(String[] args) throws IOException {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		FileInputStream fis = new FileInputStream(new File("resources/cordasconetwork.graph"));
		GraphDrawer gd = new GraphDrawer(fis, null,null);
		gd.runPartition();
	}

	public GraphDrawer(FileInputStream fpIn, FileOutputStream fpOut,GraphTraversingOrdering gto) throws IOException {
		super(fpIn, fpOut, 0, null, 0, false, gto);
	}



	public void runPartition() throws NumberFormatException, IOException {
		nodesTrav.add(null);
		nodeNumbers = -1;
		edgeNumbers = -1;
		Integer nodeCount = 1;

		//read the first line
		//go on until there are no comments
		String line = "";
		while ((line = scanner.readLine()) != null
				&&	line.length() != 0) {
			//String firstLine = scanner.nextLine().trim();
			line = line.trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			} else {
				printerOut.print(line);
				StringTokenizer strTok = new StringTokenizer(line, " ");
				//read the number of nodes
				if (strTok.hasMoreTokens()) {
					String token = strTok.nextToken();
					nodeNumbers = Integer.parseInt(token);
				}
				//read the number of edges
				if (strTok.hasMoreTokens()) {
					String token = strTok.nextToken();
					edgeNumbers = Integer.parseInt(token);
				}
				break;
			}
		}
		//graph
		this.gr = new SingleGraph("grafo");
		gr.setStrict(false);
		gr.addAttribute("ui.stylesheet", GraphPartitionator.STYLESHEET);
		int j = 0;
		//read the whole graph from file
		SpriteManager sman = new SpriteManager(gr);
		FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.HD720);
		ArrayList<Node> nodes = new ArrayList<>();
		while((line = scanner.readLine()) != null) {
			line = line.trim();
			//String line = scanner.nextLine().trim();
			if (line.startsWith("%")) { //it is a comment
				continue;
			}

			if (!nodes.isEmpty()) {
				System.out.println(nodes.size());
				nodes.stream().forEach(p -> {
					p.setAttribute("ui.color", 1.0/5);
				});
			}

			String[] nNodes = line.split(" ");
			Node v = gr.addNode(Integer.toString(nodeCount++));
			v.addAttribute("ui.color", 1.0/2);
			v.addAttribute("label", v.getId());

			pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

			for (String s : nNodes) {
				if (gr.getNode(s) == null) {
					gr.addNode(s).addAttribute("label", s);
					gr.getNode(s).addAttribute("ui.color", 1.0/100);	
				}
				gr.addEdge(v.getId()+"-"+s, v.getId(), s);					
			}

			String graphFile = " graph" + j++ +  ".png";
			//gr.display(true);
			pic.writeAll(gr, graphFile);
			nodes.add(v);
		}

	}

}
