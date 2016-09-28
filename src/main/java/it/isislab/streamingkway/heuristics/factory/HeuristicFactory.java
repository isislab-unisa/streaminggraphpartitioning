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
import it.isislab.streamingkway.heuristics.my.ExponentiallyCompAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.my.ExponentiallyCompNormWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.my.LinearCompAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.my.LinearCompNormWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.my.MyRelationshipHeuristics;
import it.isislab.streamingkway.heuristics.my.UnweightedCompAbsDispersionBased;
import it.isislab.streamingkway.heuristics.my.UnweightedCompNormDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.BalanceBigDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.ExponentiallyAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.ExponentiallyNormWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.ExponentiallyRecWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.LinearAbsWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.LinearNormWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.LinearRecWeightedDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.RelationshipHeuristics;
import it.isislab.streamingkway.heuristics.relationship.UnweightedAbsDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.UnweightedNormDispersionBased;
import it.isislab.streamingkway.heuristics.relationship.UnweightedRecDispersionBased;

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

		case MyRelationshipHeuristics.U_C_ABS_DISPERSION_BASED : heuristic = new UnweightedCompAbsDispersionBased();
		break;
		case MyRelationshipHeuristics.L_C_ABS_DISPERSION_BASED : heuristic = new LinearCompAbsWeightedDispersionBased();
		break;
		case MyRelationshipHeuristics.E_C_ABS_DISPERSION_BASED : heuristic = new ExponentiallyCompAbsWeightedDispersionBased();
		break;
		case MyRelationshipHeuristics.U_C_NORM_DISPERSION_BASED : heuristic = new UnweightedCompNormDispersionBased();
		break;
		case MyRelationshipHeuristics.L_C_NORM_DISPERSION_BASED : heuristic = new LinearCompNormWeightedDispersionBased();
		break;
		case MyRelationshipHeuristics.E_C_NORM_DISPERSION_BASED : heuristic = new ExponentiallyCompNormWeightedDispersionBased();
		break;
		case RelationshipHeuristics.U_REC_DISPERSION_BASED: heuristic = new  UnweightedRecDispersionBased(parallel);
		break;
		case RelationshipHeuristics.L_REC_DISPERSION_BASED: heuristic = new  LinearRecWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.E_REC_DISPERSION_BASED: heuristic = new  ExponentiallyRecWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.U_ABS_DISPERSION_BASED: heuristic = new  UnweightedAbsDispersionBased(parallel);
		break;
		case RelationshipHeuristics.L_ABS_DISPERSION_BASED: heuristic = new  LinearAbsWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.E_ABS_DISPERSION_BASED: heuristic = new  ExponentiallyAbsWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.U_NORM_DISPERSION_BASED: heuristic = new  UnweightedNormDispersionBased(parallel);
		break;
		case RelationshipHeuristics.L_NORM_DISPERSION_BASED: heuristic = new  LinearNormWeightedDispersionBased(parallel);
		break;
		case RelationshipHeuristics.E_NORM_DISPERSION_BASED: heuristic = new  ExponentiallyNormWeightedDispersionBased(parallel);
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
