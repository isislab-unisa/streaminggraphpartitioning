package it.isislab.streamingkway.heuristics.factory;


import it.isislab.streamingkway.exceptions.HeuristicNotFound;
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
		if (index <= 0) throw new HeuristicNotFound("Index cannot be less than 0");
		SGPHeuristic heuristic = null;

		switch(index) {
		case Heuristic.BALANCED: heuristic = new BalancedHeuristic();
		break;
		case Heuristic.CHUNKING : heuristic = new ChunkingHeuristic();
		break;
		case Heuristic.HASHING : heuristic = new HashingHeuristic();
		break;
		case Heuristic.U_DETERMINISTIC_GREEDY: heuristic = new UnweightedDeterministicGreedy();
		break;
		case Heuristic.L_DETERMINISTIC_GREEDY: heuristic = new LinearWeightedDeterministicGreedy();
		break;
		case Heuristic.E_DETERMINISTIC_GREEDY: heuristic = new ExponentiallyWeightedDeterministicGreedy();
		break;
		case Heuristic.U_RANDOMIZED_GREEDY: heuristic = new UnweightedRandomizedGreedy();
		break;
		case Heuristic.L_RANDOMIZED_GREEDY: heuristic = new LinearWeightedRandomizedGreedy();
		break;
		case Heuristic.E_RANDOMIZED_GREEDY: heuristic = new ExponentiallyWeightedRandomizedGreedy();
		break;
		case Heuristic.U_TRIANGLES: heuristic = new UnweightedTriangles();
		break;
		case Heuristic.L_TRIANGLES: heuristic = new LinearWeightedTriangles();
		break;
		case Heuristic.E_TRIANGLES: heuristic = new ExponentiallyWeightedTriangles();
		break;
		case Heuristic.BALANCE_BIG : heuristic = new BalanceBig();
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
		
		case RelationshipHeuristics.U_REC_DISPERSION_BASED: heuristic = new  UnweightedRecDispersionBased();
		break;
		case RelationshipHeuristics.L_REC_DISPERSION_BASED: heuristic = new  LinearRecWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_REC_DISPERSION_BASED: heuristic = new  ExponentiallyRecWeightedDispersionBased();
		break;
		case RelationshipHeuristics.U_ABS_DISPERSION_BASED: heuristic = new  UnweightedAbsDispersionBased();
		break;
		case RelationshipHeuristics.L_ABS_DISPERSION_BASED: heuristic = new  LinearAbsWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_ABS_DISPERSION_BASED: heuristic = new  ExponentiallyAbsWeightedDispersionBased();
		break;
		case RelationshipHeuristics.U_NORM_DISPERSION_BASED: heuristic = new  UnweightedNormDispersionBased();
		break;
		case RelationshipHeuristics.L_NORM_DISPERSION_BASED: heuristic = new  LinearNormWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_NORM_DISPERSION_BASED: heuristic = new  ExponentiallyNormWeightedDispersionBased();
		break;


		default: heuristic = new BalancedHeuristic();
		break;
		}
		return heuristic;
	}

}
