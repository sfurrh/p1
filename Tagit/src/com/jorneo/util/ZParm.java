package com.jorneo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sfurrh
 *This class takes a parameter string and marshalls it into one or more objects.
 *The parameter string follows this format:
 *pname.plength:pvalue
 *The pname must be 8 characters or less.  The following is an example of a single parameter called fname:
 *fname.4:Sean
 *The parameters can be nested to create a hierarchical object
 */
public class ZParm {
	private HashMap<String, ArrayList<ZParm>> _childrenByName;
	private ArrayList<ZParm> _children;
	private String _value;
	private String _name;
	
	static final Pattern pattern = Pattern.compile("([^.]+)\\.([0-9]+):(.*)");
	
	private boolean parseValue(String value) {
		
		Matcher matcher = pattern.matcher(value);
		if(matcher.matches()) {
			
			//this is not a simple string, it is child node(s)
			String name = matcher.group(1);
			String len = matcher.group(2);
			String val = matcher.group(3);
			String parmVal = val;
			int length = val.length();
			try {
				length = Integer.parseInt(len);
				if(length>val.length()) {
					length=val.length();
				}
				parmVal=val.substring(0,length);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			ZParm p = new ZParm(name,parmVal);
			addChild(p);
			
			if(length<val.length()) {
				//there is more after this parms value;
				parseValue(val.substring(length));
			}
		}else {
			this.setValue(value);
		}
		return true;
	}

	public ZParm(String name, String value) {
		/**
		 * Take a string of data and turn it into a zparm hierarchy
		 */
		this.parseValue(value);
		this.setName(name);
	}
	public ZParm(String name, ZParm child) {
		this.setName(name);
		this.addChild(child);
	}
	public ZParm(String name, ZParm[] kids) {
		this.setName(name);
		for(int i=0;i<kids.length;i++) {
			this.addChild(kids[i]);
		}
	}

	public String toString() {
		String value = "";
		if(_children != null && _children.size()>0) {
			for(Iterator<ZParm> i = _children.iterator();i.hasNext();) {
				value+=i.next();
			}
		}else {
			value=this._value;
		}
		int len = value.length();
		return this._name+"."+len+":"+value;
	}
	public void addChild(ZParm child) {
		if(_children==null) {
			_children = new ArrayList<ZParm>();
			_childrenByName = new HashMap<String,ArrayList<ZParm>>();
		}
		_children.add(child);
		if(_childrenByName.containsKey(child.getName())) {
			_childrenByName.get(child.getName()).add(child);
		}else {
			ArrayList<ZParm> list = new ArrayList<ZParm>();
			list.add(child);
			_childrenByName.put(child.getName(),list);	
		}
		
	}
	public ArrayList<ZParm> getChildren() {
		return _children;
	}
	public ArrayList<ZParm> getChildren(String name) {
		if(_childrenByName.containsKey(name)) {
			ArrayList<ZParm> children = _childrenByName.get(name);
			return children;
		}else {
			return null;
		}
	}
	public void setChildren(ArrayList<ZParm> _children) {
		this._children = _children;
		if(_childrenByName==null) {
			_childrenByName = new HashMap<String,ArrayList<ZParm>>();
		}
		for(Iterator<ZParm> i=_children.iterator();i.hasNext();) {
			ZParm child = i.next();
			if(_childrenByName.containsKey(child.getName())) {
				_childrenByName.get(child.getName()).add(child);
			}else {
				ArrayList<ZParm> childList = new ArrayList<ZParm>();
				childList.add(child);
				_childrenByName.put(child.getName(), childList);
			}
		}
	}
	public ZParm getChild(String name) {
		if(_childrenByName.containsKey(name)) {
			ArrayList<ZParm> children = _childrenByName.get(name);
			return children.get(0);
		}else {
			return null;
		}
	}

	public boolean hasChild(String name) {
		return _childrenByName.containsKey(name);
	}
	public String getValue() {
		return _value;
	}
	public String getValue(String dflt) {
		return getValue(dflt,true);
	}
	public String getValue(String dflt, boolean defaultOnEmpty) {
		if(_value==null) {
			return dflt;
		}else if(_value.length()==0) {
			if(defaultOnEmpty) {
				return dflt;
			}else{
				return _value;
			}
		}else {
			return _value;
		}
	}

	public void setValue(String _value) {
		this._value = _value;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}


}
