package se.ltu.M7017E.lab2.client;

import java.util.List;

import org.gstreamer.Element;

public class Tool {
	// TODO delete this method, replace with exceptions and handling
	public static void successOrDie(String message, boolean result) {
		if (!result) {
			System.err.println("Die because of " + message);
			System.exit(-1);
		}
	}

	/**
	 * Get the (first) Element from the list which name starts with 'start'. So
	 * for example if you want the element "rtpsession" no matter if it's really
	 * called "rtpsession0" or "rtpsession2", you should call this with
	 * start="rtpsession".
	 * 
	 * @param elts
	 *            list of Element_s to search in
	 * @param start
	 *            the String to search at the beginning
	 * @return the (first, if several) Element. Or null if no name matched.
	 */
	public static Element getElementByNameStartingWith(List<Element> elts,
			String start) {
		Element ret = null;

		for (Element elt : elts) {
			if (elt.getName().startsWith(start)) {
				return elt;
			}
		}

		return ret;
	}
}
