package pt.ist.socialsoftware.edition.domain;

import pt.ist.socialsoftware.edition.generators.visitors.TextTreeVisitor;

public class SubstText extends SubstText_Base {

	public SubstText(TextPortion parent) {
		parent.addChildText(this);
	}

	@Override
	public void accept(TextTreeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Boolean isFormat(Boolean displayDel, Boolean highlightSubst,
			FragInter fragInter) {
		if (getInterps().contains(fragInter) && highlightSubst) {
			return true;
		} else {
			return false;
		}
	}

}
