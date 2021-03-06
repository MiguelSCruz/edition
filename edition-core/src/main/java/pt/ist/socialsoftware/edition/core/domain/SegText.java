package pt.ist.socialsoftware.edition.core.domain;

import pt.ist.socialsoftware.edition.core.domain.SegText_Base;
import pt.ist.socialsoftware.edition.core.generators.TextPortionVisitor;

public class SegText extends SegText_Base {

	public SegText(TextPortion parent) {
		parent.addChildText(this);
	}

	@Override
	public Boolean isFormat(Boolean displayDel, Boolean highlightSubst,
			FragInter fragInter) {
		if (getInterps().contains(fragInter)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void accept(TextPortionVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void remove() {
		setAltTextWeight(null);

		super.remove();
	}

}
