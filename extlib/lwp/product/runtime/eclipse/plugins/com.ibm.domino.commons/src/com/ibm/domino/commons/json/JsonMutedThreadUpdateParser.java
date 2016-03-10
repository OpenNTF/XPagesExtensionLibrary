package com.ibm.domino.commons.json;

import java.io.Reader;
import java.text.ParseException;
import java.util.Map.Entry;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.commons.model.ModelException;
import com.ibm.domino.commons.model.MutedThreadUpdate;

public class JsonMutedThreadUpdateParser {

	final private Reader _reader;

	public JsonMutedThreadUpdateParser(final Reader reader) {
		_reader = reader;
	}

	private static final int UNID_SIZE = 32;

	private String validateUNID(final Object val) throws ModelException {
		if (val == null) {
            final String msg = StringUtil.format("Null UNID"); // $NLX-JsonMutedThreadUpdateParser.NullUNID-1$
			throw new ModelException(msg);
		}
		if (!(val instanceof String)) {
            final String msg = StringUtil.format("Bad Type UNID"); // $NLX-JsonMutedThreadUpdateParser.BadTypeUNID-1$
			throw new ModelException(msg);
		}
		final String unid = val.toString().trim().toUpperCase();
		if (unid.length() != UNID_SIZE) {
            final String msg = StringUtil.format("Invalid UNID {0}", unid); // $NLX-JsonMutedThreadUpdateParser.InvalidUNID0-1$
			throw new ModelException(msg);
		}
		for (final char c : unid.toCharArray()) {
			final boolean ok = ((c >= 'A' && c <= 'F') || (c >= '0' && c <= '9'));
			if (!ok) {
                final String msg = StringUtil.format("Invalid UNID {0}", unid); // $NLX-JsonMutedThreadUpdateParser.InvalidUNID0.1-1$
				throw new ModelException(msg);
			}
		}

		return unid;
	}

	public MutedThreadUpdate fromJson() throws JsonException, ParseException, ModelException {
		final MutedThreadUpdate result = new MutedThreadUpdate();
		final JsonJavaObject obj = (JsonJavaObject) JsonParser.fromJson(JsonJavaFactory.instanceEx, _reader);

		for (final Entry<String, Object> entry : obj.entrySet()) {
			result.setRequestAction(entry.getKey(), validateUNID(entry.getValue()));
		}
		return result;

	}
}
