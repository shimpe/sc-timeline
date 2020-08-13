ScTimeline {
	// this is a normal constructor method
	var <>internalcounter;
	var <>internallist;
	var <>internalabssched;
	var <>internalplayers;
	var <>internalplotlist;
    var <>plotter;

	*new {
		^super.new.init();
	}

	init {
		this.internalcounter = 0;
		this.internallist = [];
		this.internalabssched = [];
		this.internalplayers = ();
		this.plotter = ScTimelinePlotter();

	}

	schedBeat {
		| start_beat, stop_beat, pattern, name=nil, color=nil |
		this.internallist = this.internallist.add(Ptpar([start_beat, Pfindur(stop_beat - start_beat, pattern)]));
		if (name.isNil) {
			name = this.prInternalnewplotcounter;
		} {
			name = name.asSymbol;
		};
		this.internalplotlist = this.internalplotlist.add( (\name: name, \start: start_beat, \stop: stop_beat, \type: \beat, \color: color) );
		^nil;
	}

	schedTime {
		| delta_time, duration, pattern, name=nil, color=nil |
		this.internalabssched = this.internalabssched.add((\dt: delta_time, \dur: duration, \p: pattern, \type : \time, \color: color));
		if (name.isNil) {
			name = this.prInternalnewplotcounter;
		} {
			name = name.asSymbol;
		};
		this.internalplotlist = this.internalplotlist.add( (\name: name, \start: delta_time, \stop : delta_time + duration, \type: \time, \color: color) );
		^nil;
	}

	schedBeatNumber {
		| start_beat, no_of_events, pattern, name=nil, color=nil |
		this.internallist = this.internallist.add(Ptpar([start_beat, Pfin(no_of_events, pattern)]));
		if (name.isNil) {
			name = this.prInternalnewplotcounter;
		} {
			name = name.asSymbol;
		};
		this.internalplotlist = this.internalplotlist.add( (\name: name, \start: start_beat, \stop: no_of_events, \type: \number, \color: color) );
		^nil;
	}

	schedTimeNumber {
		| delta_time, no_of_events, pattern, name=nil, color=nil |
		if (name.isNil) {
			name = this.prInternalnewplotcounter;
		} {
			name = name.asSymbol;
		};
		this.internalabssched = this.internalabssched.add((\dt: delta_time, \dur: no_of_events, \p : pattern, \type : \number));
		this.internalplotlist = this.internalplotlist.add( (\name : name, \start: delta_time, \stop: no_of_events, \type : \number, \color: color) );
		^nil;
	}

	play {
		| tempoclock |
		if (tempoclock.isNil) {
			tempoclock = TempoClock.default;
		};
		this.internalabssched.do({
			|el|
			var cnt = this.prInternalnewcounter;
			var type = el.type;
			if (type == \time) {
				SystemClock.sched(el.dt, { this.internalplayers[cnt.asSymbol] = el.p.play(tempoclock); nil});
				SystemClock.sched(el.dt + el.dur, { this.internalplayers[cnt.asSymbol].stop; nil});
			} {
				SystemClock.sched(el.dt, { this.internalplayers[cnt.asSymbol] = Pfin(el.dur, el.p).play(tempoclock); nil});
			};
		});
		this.internalplayers[this.prInternalnewcounter.asSymbol] = Ppar(this.internallist).play(tempoclock);
		^nil;
	}

	stop {
		this.internalplayers.keysValuesDo({
			| key, value |
			value.stop;
		});
		this.internalplayers = ();
		^nil;
	}

	asView {
		^this.plotter.asView(this.internalplotlist);
	}

	plot {
		var w = Window.new(bounds:Rect(200,200,1200,300));
		w.layout_(VLayout(this.asView));
		w.front;
	}

	prInternalnewcounter {
		var cnt = this.internalcounter;
		this.internalcounter = this.internalcounter + 1;
		^cnt;
	}

	prInternalnewplotcounter {
		var cnt = this.internalcounter;
		this.internalcounter = this.internalcounter + 1;
		^cnt.asSymbol;
	}
}