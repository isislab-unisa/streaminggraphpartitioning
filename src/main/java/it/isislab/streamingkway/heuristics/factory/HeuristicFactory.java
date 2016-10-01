package it.isislab.streamingkway.heuristics.factory;


import it.isislab.streamingkway.exceptions.HeuristicNotFound;
import it.isislab.streamingkway.heuristics.AbstractDeterministicGreedyExt;
import it.isislab.streamingkway.heuristics.BalanceBig;
import it.isislab.streamingkway.heuristics.BalancedHeuristic;
import it.isislab.streamingkway.heuristics.ChunkingHeuristic;
import it.isislab.streamingkway.heuristics.ExponentiallyWeightedDeterministicGreedy;
import it.isislab.streamingkway.heuristics.ExponentiallyWeightedRandomizedGreedy;
import it.isislab.streamingkway.heuristics.ExponentiallyWeightedTriangles;
import it.isislab.streamingkway.heuristics.HashingHeuristic;
import it.isislab.streamingkway.heuristics.Heuristic;
import it.isislab.streamingkway.heuristics.LinearWeightedDeterministicGreedy;
import it.isislab.streamingkway.heuristics.LinearWeightedRandomizedGreedy;
import it.isislab.streamingkway.heuristics.LinearWeightedTriangles;
import it.isislab.streamingkway.heuristics.SGPHeuristic;
import it.isislab.streamingkway.heuristics.UnweightedDeterministicGreedy;
import it.isislab.streamingkway.heuristics.UnweightedRandomizedGreedy;
import it.isislab.streamingkway.heuristics.UnweightedTriangles;
import it.isislab.streamingkway.heuristics.relationship.BalanceBigDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.ExponentiallyAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.LabelPropagation;
import it.isislab.streamingkway.heuristics.relationship.LinearAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.NewHeuristics;
import it.isislab.streamingkway.heuristics.relationship.RelationshipHeuristics;
import it.isislab.streamingkway.heuristics.relationship.UnweightedAbsDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.newh.DispersionForKway;
import it.isislab.streamingkway.heuristics.relationship.newh.DispersionPredict;
import it.isislab.streamingkway.heuristics.relationship.newh.ExponentiallyDispersionForKway;
import it.isislab.streamingkway.heuristics.relationship.newh.ExponentiallyDispersionPredict;
import it.isislab.streamingkway.heuristics.relationship.newh.ExponentiallyIndexWithDispersion;
import it.isislab.streamingkway.heuristics.relationship.newh.ExponentiallySTCHeuristic;
import it.isislab.streamingkway.heuristics.relationship.newh.IndexWithDispersion;
import it.isislab.streamingkway.heuristics.relationship.newh.LinearDispersionForKway;
import it.isislab.streamingkway.heuristics.relationship.newh.LinearDispersionPredict;
import it.isislab.streamingkway.heuristics.relationship.newh.LinearIndexWithDispersion;
import it.isislab.streamingkway.heuristics.relationship.newh.LinearSTCHeuristic;
import it.isislab.streamingkway.heuristics.relationship.newh.STCHeuristic;

public class HeuristicFactory {
	
	public static SGPHeuristic getHeuristic(Integer index) throws HeuristicNotFound {
		return getHeuristic(index, true);
	}

	public static SGPHeuristic getHeuristic(Integer index, Boolean parallel) throws HeuristicNotFound {
		if (index <= 0) throw new HeuristicNotFound("Index cannot be less than 0");
		SGPHeuristic heuristic = null;

		switch(index) {
		case Heuristic.BALANCED: heuristic = new BalancedHeuristic(parallel);
		break;
		case Heuristic.CHUNKING : heuristic = new ChunkingHeuristic();
		break;
		case Heuristic.HASHING : heuristic = new HashingHeuristic();
		break;
		case Heuristic.U_DETERMINISTIC_GREEDY: heuristic = new UnweightedDeterministicGreedy(parallel);
		break;
		case Heuristic.L_DETERMINISTIC_GREEDY: heuristic = new LinearWeightedDeterministicGreedy(parallel);
		break;
		case Heuristic.E_DETERMINISTIC_GREEDY: heuristic = new ExponentiallyWeightedDeterministicGreedy(parallel);
		break;
		case Heuristic.U_RANDOMIZED_GREEDY: heuristic = new UnweightedRandomizedGreedy(parallel);
		break;
		case Heuristic.L_RANDOMIZED_GREEDY: heuristic = new LinearWeightedRandomizedGreedy(parallel);
		break;
		case Heuristic.E_RANDOMIZED_GREEDY: heuristic = new ExponentiallyWeightedRandomizedGreedy(parallel);
		break;
		case Heuristic.U_TRIANGLES: heuristic = new UnweightedTriangles(parallel);
		break;
		case Heuristic.L_TRIANGLES: heuristic = new LinearWeightedTriangles(parallel);
		break;
		case Heuristic.E_TRIANGLES: heuristic = new ExponentiallyWeightedTriangles(parallel);
		break;
		case Heuristic.BALANCE_BIG : heuristic = new BalanceBig(parallel);
		break;

		case RelationshipHeuristics.U_ABS_DISPERSION_BASED : heuristic = new UnweightedAbsDispersionBased(parallel);
		break;
		case RelationshipHeuristics.L_ABS_DISPERSION_BASED : heuristic = new LinearAbsWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.E_ABS_DISPERSION_BASED : heuristic = new ExponentiallyAbsWeightedDispersionBased(parallel);
		break;
		case NewHeuristics.U_DISP_KWAY : heuristic = new DispersionForKway(parallel);
		break;
		case NewHeuristics.L_DISP_KWAY : heuristic = new LinearDispersionForKway(parallel);
		break;
		case NewHeuristics.E_DISP_KWAY : heuristic = new ExponentiallyDispersionForKway(parallel);
		break;
		case NewHeuristics.U_DISP_PREDICT : heuristic = new DispersionPredict(parallel);
		break;
		case NewHeuristics.L_DISP_PREDICT : heuristic = new LinearDispersionPredict(parallel);
		break;
		case NewHeuristics.E_DISP_PREDICT : heuristic = new ExponentiallyDispersionPredict(parallel);
		break;
		case NewHeuristics.U_DISP_IND : heuristic = new IndexWithDispersion(parallel);
		break;
		case NewHeuristics.L_DISP_IND : heuristic = new LinearIndexWithDispersion(parallel);
		break;
		case NewHeuristics.E_DISP_IND : heuristic = new ExponentiallyIndexWithDispersion(parallel);
		break;
		case NewHeuristics.L_PROPAG : heuristic = new LabelPropagation(parallel);
		break;
		case NewHeuristics.U_STC : heuristic = new STCHeuristic(parallel);
		break;
		case NewHeuristics.L_STC : heuristic = new LinearSTCHeuristic(parallel);
		break;
		case NewHeuristics.E_STC : heuristic = new ExponentiallySTCHeuristic(parallel);
		break;
		case RelationshipHeuristics.BB_DISPERSION_BASED: heuristic = new BalanceBigDispersionBased(parallel);
		break;
		case Heuristic.U_DETERMINISTIC_GREEDY_EXP : heuristic = new AbstractDeterministicGreedyExt(parallel);
		break;


		default: heuristic = new BalancedHeuristic(parallel);
		break;
		}
		return heuristic;
	}

}
