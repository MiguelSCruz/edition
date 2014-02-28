package pt.ist.socialsoftware.edition.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ist.socialsoftware.edition.domain.AddText;
import pt.ist.socialsoftware.edition.domain.AltText;
import pt.ist.socialsoftware.edition.domain.AppText;
import pt.ist.socialsoftware.edition.domain.DelText;
import pt.ist.socialsoftware.edition.domain.FragInter;
import pt.ist.socialsoftware.edition.domain.GapText;
import pt.ist.socialsoftware.edition.domain.LbText;
import pt.ist.socialsoftware.edition.domain.ParagraphText;
import pt.ist.socialsoftware.edition.domain.PbText;
import pt.ist.socialsoftware.edition.domain.RdgGrpText;
import pt.ist.socialsoftware.edition.domain.RdgText;
import pt.ist.socialsoftware.edition.domain.Rend;
import pt.ist.socialsoftware.edition.domain.Rend.Rendition;
import pt.ist.socialsoftware.edition.domain.SegText;
import pt.ist.socialsoftware.edition.domain.SimpleText;
import pt.ist.socialsoftware.edition.domain.SpaceText;
import pt.ist.socialsoftware.edition.domain.SpaceText.SpaceDim;
import pt.ist.socialsoftware.edition.domain.SubstText;
import pt.ist.socialsoftware.edition.domain.Surface;
import pt.ist.socialsoftware.edition.domain.TextPortion;
import pt.ist.socialsoftware.edition.domain.UnclearText;

public class HtmlWriter4OneInter extends HtmlWriter {
	protected FragInter fragInter = null;
	protected String transcription = "";

	private void append2Transcription(String generated) {
		if (generate) {
			transcription = transcription + generated;
		}
	}

	protected Boolean highlightDiff = false;
	protected Boolean displayDel = false;
	protected Boolean highlightIns = true;
	protected Boolean highlightSubst = false;
	protected Boolean showNotes = true;

	private boolean generate = true;
	private PbText startPbText = null;
	private PbText stopPbText = null;

	private final Map<FragInter, Integer> interpsChar = new HashMap<FragInter, Integer>();
	private int totalChar = 0;

	public String getTranscription() {
		return transcription;
	}

	public Integer getInterPercentage(FragInter inter) {
		return (interpsChar.get(inter) * 100) / totalChar;
	}

	public HtmlWriter4OneInter(FragInter fragInter) {
		this.fragInter = fragInter;
		transcription = "";

		for (FragInter inter : fragInter.getFragment().getFragmentInterSet()) {
			interpsChar.put(inter, 0);
		}
	}

	public void write(Boolean highlightDiff) {
		this.highlightDiff = highlightDiff;
		if (fragInter.getLastUsed() != fragInter) {
			fragInter = fragInter.getLastUsed();
		}
		visit((AppText) fragInter.getFragment().getTextPortion());
	}

	public void write(Boolean highlightDiff, Boolean displayDel,
			Boolean highlightIns, Boolean highlightSubst, Boolean showNotes,
			Surface surface) {
		this.highlightDiff = highlightDiff;
		this.displayDel = displayDel;
		this.highlightIns = highlightIns;
		this.highlightSubst = highlightSubst;
		this.showNotes = showNotes;
		if (fragInter.getLastUsed() != fragInter) {
			fragInter = fragInter.getLastUsed();
		}

		if (surface != null) {
			startPbText = surface.getPbText();
			if (startPbText != null) {
				generate = false;
			}
			Surface stopSurface = surface.getNext();
			if (stopSurface == null) {
				stopPbText = null;
			} else {
				stopPbText = stopSurface.getPbText();
			}
		}
		visit((AppText) fragInter.getFragment().getTextPortion());
	}

	@Override
	public void visit(AppText appText) {
		propagate2FirstChild(appText);

		if (appText.getParentOfLastText() == null) {
			propagate2NextSibling(appText);
		}
	}

	@Override
	public void visit(RdgGrpText rdgGrpText) {
		if (rdgGrpText.getInterps().contains(this.fragInter)) {
			propagate2FirstChild(rdgGrpText);
		}

		propagate2NextSibling(rdgGrpText);
	}

	@Override
	public void visit(RdgText rdgText) {
		if (rdgText.getInterps().contains(this.fragInter)) {

			Boolean color = false;
			if (highlightDiff) {
				int size = fragInter.getFragment().getFragmentInterSet().size();
				if (rdgText.getInterps().size() < size) {
					color = true;
					int colorValue = 255 - (255 / size)
							* (size - rdgText.getInterps().size() - 1);
					String colorCode = "<span style=\"background-color: rgb(0,"
							+ colorValue + ",255);\">";

					append2Transcription(rdgText.writeSeparator(displayDel,
							highlightSubst, fragInter) + colorCode);
				}
			}

			if (!color) {
				append2Transcription(rdgText.writeSeparator(displayDel,
						highlightSubst, fragInter));
			}

			propagate2FirstChild(rdgText);

			if (color) {
				append2Transcription("</span>");
			}
		}

		propagate2NextSibling(rdgText);
	}

	@Override
	public void visit(ParagraphText paragraphText) {
		append2Transcription("<p align=\"justify\">");

		propagate2FirstChild(paragraphText);

		append2Transcription("</p>");

		propagate2NextSibling(paragraphText);
	}

	@Override
	public void visit(SegText segText) {
		List<Rend> renditions = new ArrayList<Rend>(segText.getRendSet());
		String preRend = generatePreRendition(renditions);
		String postRend = generatePostRendition(renditions);

		String altRend = "";
		if (segText.getAltTextOne() != null) {
			altRend = "<span class=\"text-warning\">" + "<abbr title=\""
					+ segText.getAltTextOne().getMode().getDesc() + " "
					+ segText.getAltTextOne().getWeightOne() + "\">";
		} else if (segText.getAltTextTwo() != null) {
			altRend = "<span class=\"text-warning\">" + "<abbr title=\""
					+ segText.getAltTextTwo().getMode().getDesc() + " "
					+ segText.getAltTextTwo().getWeightTwo() + "\">";

		}

		append2Transcription(segText.writeSeparator(displayDel, highlightSubst,
				fragInter) + preRend + altRend);

		propagate2FirstChild(segText);

		if ((segText.getAltTextOne()) != null
				|| (segText.getAltTextTwo() != null)) {
			altRend = "</abbr></span>";
		}

		append2Transcription(altRend + postRend);

		propagate2NextSibling(segText);
	}

	@Override
	public void visit(AltText altText) {
		// do nothing, the segTextOne and segTextTwo will do
		propagate2NextSibling(altText);
	}

	@Override
	public void visit(SimpleText simpleText) {
		String value = simpleText.getValue();

		totalChar = totalChar + value.length();
		for (FragInter inter : simpleText.getInterps()) {
			Integer number = interpsChar.get(inter);
			number = number + value.length();
			interpsChar.put(inter, number);
		}

		append2Transcription(simpleText.writeSeparator(displayDel,
				highlightSubst, fragInter) + value);

		propagate2NextSibling(simpleText);
	}

	@Override
	public void visit(LbText lbText) {
		if (lbText.getInterps().contains(fragInter)) {
			String hyphen = "";
			if (lbText.getHyphenated()) {
				hyphen = "-";
			}

			append2Transcription(hyphen + "<br>");
		}

		propagate2NextSibling(lbText);
	}

	@Override
	public void visit(PbText pbText) {
		if (pbText.getInterps().contains(fragInter)) {
			if ((startPbText != pbText) && (stopPbText != pbText)) {
				append2Transcription("<hr size=\"4\" color=\"black\">");
			}
		}

		if (startPbText == pbText) {
			generate = true;
		}

		if (stopPbText == pbText) {
			generate = false;
		}

		propagate2NextSibling(pbText);
	}

	@Override
	public void visit(SpaceText spaceText) {
		String separator = "";
		if (spaceText.getDim() == SpaceDim.VERTICAL) {
			separator = "<br>";
			// the initial line break is for a new line
			append2Transcription(separator);
		} else if (spaceText.getDim() == SpaceDim.HORIZONTAL) {
			separator = "&nbsp; ";
		}

		for (int i = 0; i < spaceText.getQuantity(); i++) {
			append2Transcription(separator);
		}

		propagate2NextSibling(spaceText);
	}

	@Override
	public void visit(AddText addText) {
		List<Rend> renditions = new ArrayList<Rend>(addText.getRendSet());
		String preRendition = generatePreRendition(renditions);
		String postRendition = generatePostRendition(renditions);

		String prePlaceFormat = "";
		String postPlaceFormat = "";
		switch (addText.getPlace()) {
		case INLINE:
		case INSPACE:
		case OVERLEAF:
		case SUPERIMPOSED:
		case MARGIN:
		case OPPOSITE:
		case BOTTOM:
		case END:
		case UNSPECIFIED:
			prePlaceFormat = "<small>";
			postPlaceFormat = "</small>";
			break;
		case ABOVE:
		case TOP:
			prePlaceFormat = "<span style=\"position:relative; top:-3px;\">";
			postPlaceFormat = "</span>";
			// prePlaceFormat = "<sup>";
			// postPlaceFormat = "</sup>";
			break;
		case BELOW:
			prePlaceFormat = "<span style=\"position:relative; top:3px;\">";
			postPlaceFormat = "</span>";
			// prePlaceFormat = "<sub>";
			// postPlaceFormat = "</sub>";
			break;
		}

		if (highlightIns) {
			String insertSymbol = "<span style=\"color: rgb(128,128,128);\"><small>&and;</small></span>";
			if (showNotes) {
				insertSymbol = "<abbr title=\"" + addText.getNote() + "\">"
						+ insertSymbol + "</abbr>";
			}

			append2Transcription(addText.writeSeparator(displayDel,
					highlightSubst, fragInter)
					+ preRendition
					+ prePlaceFormat
					+ insertSymbol);
		} else {
			append2Transcription(addText.writeSeparator(displayDel,
					highlightSubst, fragInter));
		}

		propagate2FirstChild(addText);

		if (highlightIns) {
			append2Transcription(postPlaceFormat + postRendition);
		}

		propagate2NextSibling(addText);
	}

	@Override
	public void visit(DelText delText) {
		if (displayDel) {
			append2Transcription(delText.writeSeparator(displayDel,
					highlightSubst, fragInter)
					+ "<del><span style=\"color: rgb(128,128,128);\">");
			if (showNotes) {
				append2Transcription("<abbr title=\"" + delText.getNote()
						+ "\">");
			}

			propagate2FirstChild(delText);

			if (showNotes) {
				append2Transcription("</abbr>");
			}

			append2Transcription("</span></del>");
		}

		propagate2NextSibling(delText);
	}

	@Override
	public void visit(SubstText substText) {
		if (displayDel && highlightSubst) {
			append2Transcription(substText.writeSeparator(displayDel,
					highlightSubst, fragInter)
					+ "<span style=\"color: rgb(0,0,255);\">[</span>");
		}

		propagate2FirstChild(substText);

		if (displayDel && highlightSubst) {
			append2Transcription("<span style=\"color: rgb(0,0,255);\">]"
					+ "<sub>subst</sub></span>");
		}

		propagate2NextSibling(substText);
	}

	@Override
	public void visit(GapText gapText) {
		String gapValue = gapText.getGapValue();

		totalChar = totalChar + gapValue.length();
		for (FragInter inter : gapText.getInterps()) {
			Integer number = interpsChar.get(inter);
			number = number + gapValue.length();
			interpsChar.put(inter, number);
		}

		append2Transcription(gapText.writeSeparator(displayDel, highlightSubst,
				fragInter)
				+ "<abbr title=\""
				+ gapText.getReason().getDesc()
				+ ", "
				+ gapText.getExtent()
				+ " "
				+ gapText.getUnit()
				+ "\">"
				+ gapValue + "</abbr>");

		propagate2NextSibling(gapText);
	}

	@Override
	public void visit(UnclearText unclearText) {
		append2Transcription(unclearText.writeSeparator(displayDel,
				highlightSubst, fragInter)
				+ "<span style=\"text-shadow: black 0.0em 0.0em 0.1em; -webkit-filter: blur(0.005em);\">"
				+ "<abbr title=\"" + unclearText.getReason().getDesc() + "\">");

		propagate2FirstChild(unclearText);

		append2Transcription("</abbr>" + "</span>");

		propagate2NextSibling(unclearText);
	}

	private void propagate2FirstChild(TextPortion text) {
		TextPortion firstChild = text.getFirstChildText();
		if (firstChild != null) {
			firstChild.accept(this);
		}
	}

	private void propagate2NextSibling(TextPortion text) {
		if (text.getNextText() != null) {
			text.getNextText().accept(this);
		}
	}

	private String generatePreRendition(List<Rend> renditions) {
		String preRend = "";
		for (Rend rend : renditions) {
			// the order matters
			if (rend.getRend() == Rendition.RIGHT) {
				preRend = "<div class=\"text-right\">" + preRend;
			} else if (rend.getRend() == Rendition.LEFT) {
				preRend = "<div class=\"text-left\">" + preRend;
			} else if (rend.getRend() == Rendition.CENTER) {
				preRend = "<div class=\"text-center\">" + preRend;
			} else if (rend.getRend() == Rendition.BOLD) {
				preRend = preRend + "<strong>";
			} else if (rend.getRend() == Rendition.ITALIC) {
				preRend = preRend + "<em>";
			} else if (rend.getRend() == Rendition.RED) {
				preRend = preRend + "<span style=\"color: rgb(255,0,0);\">";
			} else if (rend.getRend() == Rendition.UNDERLINED) {
				preRend = preRend + "<u>";
			} else if (rend.getRend() == Rendition.SUPERSCRIPT) {
				preRend = preRend + "<sup>";
			} else if (rend.getRend() == Rendition.SUBSCRIPT) {
				preRend = preRend + "<sub>";
			}
		}
		return preRend;
	}

	private String generatePostRendition(List<Rend> renditions) {
		String postRend = "";
		for (Rend rend : renditions) {
			if (rend.getRend() == Rendition.RIGHT) {
				postRend = postRend + "</div>";
			} else if (rend.getRend() == Rendition.LEFT) {
				postRend = postRend + "</div>";
			} else if (rend.getRend() == Rendition.CENTER) {
				postRend = postRend + "</div>";
			} else if (rend.getRend() == Rendition.BOLD) {
				postRend = "</strong>" + postRend;
			} else if (rend.getRend() == Rendition.ITALIC) {
				postRend = "</em>" + postRend;
			} else if (rend.getRend() == Rendition.RED) {
				postRend = "</span>" + postRend;
			} else if (rend.getRend() == Rendition.UNDERLINED) {
				postRend = "</u>" + postRend;
			} else if (rend.getRend() == Rendition.SUPERSCRIPT) {
				postRend = "</sup>" + postRend;
			} else if (rend.getRend() == Rendition.SUBSCRIPT) {
				postRend = "</sub>" + postRend;
			}
		}
		return postRend;
	}

}