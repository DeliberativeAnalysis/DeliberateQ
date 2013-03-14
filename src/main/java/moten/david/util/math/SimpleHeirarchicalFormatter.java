package moten.david.util.math;

public interface SimpleHeirarchicalFormatter {
	public void header(String s, boolean collapsed);

	public void blockStart();

	public void item(Object object);

	public void link(String s, String id, Object object, String action);

	public void image(String s, String id, Object object, String action);

	public void blockFinish();

}
