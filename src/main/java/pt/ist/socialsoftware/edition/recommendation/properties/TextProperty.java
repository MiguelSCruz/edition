package pt.ist.socialsoftware.edition.recommendation.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.ist.socialsoftware.edition.domain.ExpertEditionInter;
import pt.ist.socialsoftware.edition.domain.FragInter;
import pt.ist.socialsoftware.edition.domain.Fragment;
import pt.ist.socialsoftware.edition.domain.RecommendationWeights;
import pt.ist.socialsoftware.edition.domain.SourceInter;
import pt.ist.socialsoftware.edition.search.Indexer;
import pt.ist.socialsoftware.edition.shared.exception.LdoDException;

public class TextProperty extends Property {
	public static final int NUMBER_OF_TERMS = 100;

	private static Map<String, List<Double>> vectors = new HashMap<String, List<Double>>();

	private static Map<String, List<String>> commonTermsMap = new HashMap<String, List<String>>();

	private String idPair;
	private List<String> commonTerms;

	public TextProperty(double weigth) {
		super(weigth);
	}

	public TextProperty(@JsonProperty("weight") String weight) {
		this(Double.parseDouble(weight));
	}

	@Override
	public void prepareToLoadProperty(FragInter inter1, FragInter inter2) {
		idPair = generateID(inter1, inter2);
		commonTerms = commonTermsMap.get(idPair);
		if (commonTerms == null) {
			try {
				Indexer indexer = Indexer.getIndexer();
				Set<String> temp = new HashSet<String>();
				temp.addAll(indexer.getTFIDFTerms(inter1, NUMBER_OF_TERMS));
				temp.addAll(indexer.getTFIDFTerms(inter2, NUMBER_OF_TERMS));
				commonTerms = new ArrayList<String>(temp);
				commonTermsMap.put(idPair, commonTerms);
			} catch (ParseException | IOException e) {
				throw new LdoDException("prepareToLoadProperty in class TextProperty failed when invoking indexer");
			}
		}
	}

	@Override
	public void prepareToLoadProperty(Fragment frag1, Fragment frag2) {
		idPair = generateID(frag1, frag2);
		commonTerms = commonTermsMap.get(idPair);
		if (commonTerms == null) {
			try {
				Indexer indexer = Indexer.getIndexer();
				Set<String> temp = new HashSet<String>();
				temp.addAll(indexer.getTFIDFTerms(frag1, NUMBER_OF_TERMS));
				temp.addAll(indexer.getTFIDFTerms(frag2, NUMBER_OF_TERMS));
				this.commonTerms = new ArrayList<String>(temp);
				commonTermsMap.put(idPair, commonTerms);
			} catch (ParseException | IOException e) {
				throw new LdoDException("prepareToLoadProperty in class TextProperty failed when invoking indexer");
			}
		}
	}

	private String generateID(pt.ist.fenixframework.backend.jvstmojb.pstm.OneBoxDomainObject objectOne,
			pt.ist.fenixframework.backend.jvstmojb.pstm.OneBoxDomainObject objectTwo) {
		if (objectOne.getExternalId().compareTo(objectTwo.getExternalId()) < 0) {
			return objectOne.getExternalId() + objectTwo.getExternalId();
		} else {
			return objectTwo.getExternalId() + objectOne.getExternalId();
		}
	}

	private List<Double> buildVector(Map<String, Double> tfidf) {
		List<Double> vector = getDefaultVector();
		for (int i = 0; i < commonTerms.size(); i++) {
			String term = commonTerms.get(i);
			if (tfidf.containsKey(term)) {
				vector.set(i, tfidf.get(term));
			}
		}
		return vector;
	}

	private List<Double> applyWeight(List<Double> vector) {
		for (int i = 0; i < vector.size(); i++) {
			vector.set(i, getWeight() * vector.get(i));
		}
		return vector;
	}

	@Override
	protected List<Double> extractVector(ExpertEditionInter expertEditionInter) {
		List<Double> vector = vectors.get(expertEditionInter.getExternalId() + idPair);
		if (vector == null) {
			Map<String, Double> tfidf;
			try {
				tfidf = Indexer.getIndexer().getTFIDF(expertEditionInter, commonTerms);
			} catch (IOException | ParseException e) {
				throw new LdoDException("Indexer error when extractVector in TextProperty");
			}
			vector = buildVector(tfidf);
			vectors.put(expertEditionInter.getExternalId() + idPair, vector);
		}
		return applyWeight(new ArrayList<Double>(vector));
	}

	@Override
	protected List<Double> extractVector(SourceInter sourceInter) {
		List<Double> vector = vectors.get(sourceInter.getExternalId() + idPair);
		if (vector == null) {
			Map<String, Double> tfidf;
			try {
				tfidf = Indexer.getIndexer().getTFIDF(sourceInter, commonTerms);
			} catch (IOException | ParseException e) {
				throw new LdoDException("Indexer error when extractVector in TextProperty");
			}
			vector = buildVector(tfidf);
			vectors.put(sourceInter.getExternalId() + idPair, vector);
		}
		return applyWeight(new ArrayList<Double>(vector));
	}

	@Override
	protected List<Double> extractVector(Fragment fragment) {
		List<Double> vector = vectors.get(fragment.getExternalId() + idPair);
		if (vector == null) {
			Map<String, Double> tfidf;
			try {
				tfidf = Indexer.getIndexer().getTFIDF(fragment, commonTerms);
			} catch (IOException | ParseException e) {
				throw new LdoDException("Indexer error when extractVector in TextProperty");
			}
			vector = buildVector(tfidf);
			vectors.put(fragment.getExternalId() + idPair, vector);
		}
		return applyWeight(new ArrayList<Double>(vector));
	}

	@Override
	protected List<Double> getDefaultVector() {
		return new ArrayList<Double>(Collections.nCopies(commonTerms.size(), 0.0));
	}

	@Override
	public void userWeights(RecommendationWeights recommendationWeights) {
		recommendationWeights.setTextWeight(getWeight());
	}

	@Override
	public String getTitle() {
		return "Text";
	}

}