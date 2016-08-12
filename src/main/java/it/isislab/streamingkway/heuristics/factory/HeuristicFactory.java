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
		SGPHeuristic euristic = null;

		switch(index) {
		case Heuristic.BALANCED: euristic = new BalancedHeuristic();
		break;
		case Heuristic.CHUNKING : euristic = new ChunkingHeuristic();
		break;
		case Heuristic.HASHING : euristic = new HashingHeuristic();
		break;
		case Heuristic.U_DETERMINISTIC_GREEDY: euristic = new UnweightedDeterministicGreedy();
		break;
		case Heuristic.L_DETERMINISTIC_GREEDY: euristic = new LinearWeightedDeterministicGreedy();
		break;
		case Heuristic.E_DETERMINISTIC_GREEDY: euristic = new ExponentiallyWeightedDeterministicGreedy();
		break;
		case Heuristic.U_RANDOMIZED_GREEDY: euristic = new UnweightedRandomizedGreedy();
		break;
		case Heuristic.L_RANDOMIZED_GREEDY: euristic = new LinearWeightedRandomizedGreedy();
		break;
		case Heuristic.E_RANDOMIZED_GREEDY: euristic = new ExponentiallyWeightedRandomizedGreedy();
		break;
		case Heuristic.U_TRIANGLES: euristic = new UnweightedTriangles();
		break;
		case Heuristic.L_TRIANGLES: euristic = new LinearWeightedTriangles();
		break;
		case Heuristic.E_TRIANGLES: euristic = new ExponentiallyWeightedTriangles();
		break;
		case Heuristic.BALANCE_BIG : euristic = new BalanceBig();
		break;


		case RelationshipHeuristics.U_REC_DISPERSION_BASED: euristic = new  UnweightedRecDispersionBased();
		break;
		case RelationshipHeuristics.L_REC_DISPERSION_BASED: euristic = new  LinearRecWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_REC_DISPERSION_BASED: euristic = new  ExponentiallyRecWeightedDispersionBased();
		break;
		case RelationshipHeuristics.U_ABS_DISPERSION_BASED: euristic = new  UnweightedAbsDispersionBased();
		break;
		case RelationshipHeuristics.L_ABS_DISPERSION_BASED: euristic = new  LinearAbsWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_ABS_DISPERSION_BASED: euristic = new  ExponentiallyAbsWeightedDispersionBased();
		break;
		case RelationshipHeuristics.U_NORM_DISPERSION_BASED: euristic = new  UnweightedNormDispersionBased();
		break;
		case RelationshipHeuristics.L_NORM_DISPERSION_BASED: euristic = new  LinearNormWeightedDispersionBased();
		break;
		case RelationshipHeuristics.E_NORM_DISPERSION_BASED: euristic = new  ExponentiallyNormWeightedDispersionBased();
		break;


		default: euristic = new BalancedHeuristic();
		break;
		}
		return euristic;
	}

}
