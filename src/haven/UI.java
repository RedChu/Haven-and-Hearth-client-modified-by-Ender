package haven;

import java.util.*;
import java.awt.event.KeyEvent;

public class UI {
	Widget root;
	Widget keyfocus, mousefocus;
	Map<Integer, Widget> widgets = new TreeMap<Integer, Widget>();
	Map<Widget, Integer> rwidgets = new HashMap<Widget, Integer>();
	Receiver rcvr;
	
	public interface Receiver {
		public void rcvmsg(int widget, String msg, Object... args);
	}
	
	public UI(RootWidget root) {
		root.setui(this);
		this.root = root;
		widgets.put(0, root);
	}
	
	public void setreceiver(Receiver rcvr) {
		this.rcvr = rcvr;
	}
	
	public void newwidget(int id, String type, Coord c, int parent, Object... args) {
		synchronized(this) {
			Widget wdg = Widget.create(type, c, widgets.get(parent), args);
			widgets.put(id, wdg);
			rwidgets.put(wdg, id);
		}
	}
	
	private void removeid(Widget wdg) {
		int id = rwidgets.get(wdg);
		widgets.remove(id);
		rwidgets.remove(wdg);
		for(Widget child = wdg.child; child != null; child = child.next)
			removeid(child);
	}
	
	public void destroy(int id) {
		if(widgets.containsKey(id)) {
			Widget wdg = widgets.get(id);
			removeid(wdg);
			wdg.unlink();
		}
	}
	
	public void wdgmsg(Widget sender, String msg, Object... args) {
		if(rcvr != null)
			rcvr.rcvmsg(rwidgets.get(sender), msg, args);
	}
	
	public void uimsg(int id, String msg, Object... args) {
		if(widgets.containsKey(id))
			widgets.get(id).uimsg(msg.intern(), args);
	}
	
	public void type(char c) {
		if(keyfocus == null)
			root.type(c);
		else
			keyfocus.type(c);
	}
	
	public void keydown(KeyEvent ev) {
		if(keyfocus == null)
			root.keydown(ev);
		else
			keyfocus.keydown(ev);
	}
	
	public void keyup(KeyEvent ev) {
		if(keyfocus == null)
			root.keyup(ev);
		else
			keyfocus.keyup(ev);		
	}
}