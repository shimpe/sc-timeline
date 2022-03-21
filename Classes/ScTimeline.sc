/*
[general]
title = "ScTimeline"
summary = "a timeline for supercollider patterns"
categories = "Patterns"
related = "Classes/ScTimelinePlotter"
description = '''
ScTimeline provides a way to schedule patterns in time. Patterns can be scheduled on beats or on absolute times.
By wrapping Synths that play buffers in Pmono, it can also be used to algorithmically generate soundscapes.
'''
*/

ScTimeline {
	/*
	[method.internalcounter]
	description='''
	an internal counter, used to track internally generated event stream players (internal usage only)
	'''
	[method.internalcounter.returns]
	what = "a unique integer"
	*/
	var <>internalcounter;
	/*
	[method.internallist]
	description='''
	internal list to store internally generated patterns (internal usage only)
	'''
	[method.internallist.returns]
	what = "a list"
	*/
	var <>internallist;
	/*
	[method.internalabssched]
	description='''
	list to store internally generated abs time scheduling information (internal usage only)
	'''
	[method.internalabssched.returns]
	what = "a list"
	*/
	var <>internalabssched;
	/*
	[method.internalplayers]
	description='''
	dictionary to store internally generated event stream players (internal usage only)
	'''
	[method.internalplayers.returns]
	what = "an event (dictionary)"
	*/
	var <>internalplayers;
	/*
	[method.internalplotlist]
	description='''
	list to store internally generated data, used to plot the timeline (internal usage only)
	'''
	[method.internalplotlist.returns]
	what = "a list"
	*/
	var <>internalplotlist;
	/*
	[method.plotter]
	description='''
	an ScTimelinePlotter object that knows how to plot the timing data as a timeline (internal usage only)
	'''
	[method.plotter.returns]
	what = "an ScTimelinePlotter instance"
	*/
    var <>plotter;

	/*
	[classmethod.new]
	description = "New creates a new ScTimeline"
	[classmethod.new.returns]
	what = "a new ScTimeline"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = '''
	Initializes internal data structures
	'''
	*/
	init {
		this.internalcounter = 0;
		this.internallist = [];
		this.internalplotlist = [];
		this.internalabssched = [];
		this.internalplayers = ();
		this.plotter = ScTimelinePlotter();

	}

	/*
	[method.schedBeat]
	description = '''
	Runs a pattern from a given start_beat to a given stop_beat. Infinitely running patterns are cut off at the stop_beat. You can specify a name and a color which will be used when plotting the timeline.
	'''
	[method.schedBeat.args]
	start_beat = "beat at which the pattern should start playing"
	stop_beat = "beat at which the pattern should stop playing (will be cut off if needed)"
	pattern = "the pattern (a Pbind or Pmono-like pattern) to schedule"
	name = "a string that will be used when plotting the timeline. All entries with the same name are plotted in the same lane."
	color = "a supercollider Color that will be used when plotting the timeline"
	[method.schedBeat.returns]
	what = "nil"
	*/
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

	/*
	[method.schedTime]
	description = '''
	Runs a pattern from a given start_time (in seconds) for a given duration. Infinitely running patterns are cut off after duration. You can specify a name and a color which will be used when plotting the timeline.
	'''
	[method.schedTime.args]
	delta_time = "time at which the pattern should be started"
	duration = "duration after which the pattern should be stopped"
	pattern = "the pattern (a Pbind or Pmono-like pattern) to schedule"
	name = "a string that will be used when plotting the timeline. All entries with the same name are plotted in the same lane."
	color = "a supercollider Color that will be used when plotting the timeline"
	[method.schedTime.returns]
	what = "nil"
	*/
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

	/*
	[method.schedTimeFromTo]
	description = '''
	Runs a pattern from a given start_time (in seconds) to a given stop_time (in seconds). Infinitely running patterns are stopped after stop_time. You can specify a name and a color which will be used when plotting the timeline.
	'''
	[method.schedTimeFromTo.args]
	delta_time_start = "time at which the pattern should be started"
	delta_time_stop = "time at which the pattern should be stopped"
	pattern = "the pattern (a Pbind or Pmono-like pattern) to schedule"
	name = "a string that will be used when plotting the timeline. All entries with the same name are plotted in the same lane."
	color = "a supercollider Color that will be used when plotting the timeline"
	*/
	schedTimeFromTo {
		| delta_time_start, delta_time_stop, pattern, name=nil, color=nil |
		this.schedTime(delta_time_start, delta_time_stop - delta_time_start, pattern, name, color);
	}

	/*
	[method.schedBeatNumber]
	description = '''
	Runs a pattern from a given beat until it has produced a given number of events. Longer running patterns are cut off if needed. You can specify a name and a color which will be used when plotting the timeline.
	'''
	[method.schedBeatNumber.args]
	start_beat = "beat at which the pattern should be started"
	no_of_events = "max number of events after which the pattern should be stopped"
	pattern = "the pattern (a Pbind or Pmono-like pattern) to schedule"
	name = "a string that will be used when plotting the timeline. All entries with the same name are plotted in the same lane."
	color = "a supercollider Color that will be used when plotting the timeline"
	[method.schedBeatNumber.returns]
	what = "nil"
	*/
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

	/*
	[method.schedTimeNumber]
	description = '''
	Runs a pattern from a given start_time (in seconds) until it has produced no_of_events events. Longer running patterns are cut off after producing no_of_events events. You can specify a name and a color which will be used when plotting the timeline.
	'''
	[method.schedTimeNumber.args]
	delta_time = "time at which the pattern should be started"
	no_of_events = "number of events after which the pattern should be stopped"
	pattern = "the pattern (a Pbind or Pmono-like pattern) to schedule"
	name = "a string that will be used when plotting the timeline. All entries with the same name are plotted in the same lane."
	color = "a supercollider Color that will be used when plotting the timeline"
	[method.schedTimeNumber.returns]
	what = "nil"
	*/
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

	/*
	[method.play]
	description = '''
	Plays the timeline, starting and stopping the registered patterns as needed.
	'''
	[method.play.args]
	tempoclock = a Tempoclock (if none is passed, TempoClock.default is used). Note that TempoClock only affects the patterns scheduled with beat numbers. Those scheduled using time will respect the requested time.
	[method.play.returns]
	what = "nil"
	*/
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
		if (this.internallist != []) {
			this.internalplayers[this.prInternalnewcounter.asSymbol] = Ppar(this.internallist).play(tempoclock);
		};
		^nil;
	}

	/*
	[method.playAbsAfter]
	description = '''
	Plays only those patterns scheduled using times, that were scheduled after start_time. Useful to skip the beginning of a long sequence of patterns while developing longer pieces.
	'''
	[method.playAbsAfter.args]
	tempoclock = "a TempoClock. If none is provided, TempoClock.default will be used."
	start_time = "time to skip"
	[method.playAbsAfter.returns]
	what = "nil"
	*/
	playAbsAfter {
		| tempoclock, start_time |
		if (tempoclock.isNil) {
			tempoclock = TempoClock.default;
		};
		this.internalabssched.do({
			|el|
			var cnt = this.prInternalnewcounter;
			var type = el.type;
			if (el.dt >= start_time) {
				if (type == \time) {
					SystemClock.sched(el.dt - start_time, { this.internalplayers[cnt.asSymbol] = el.p.play(tempoclock); nil});
					SystemClock.sched(el.dt - start_time + el.dur, { this.internalplayers[cnt.asSymbol].stop; nil});
				} {
					SystemClock.sched(el.dt - start_time, { this.internalplayers[cnt.asSymbol] = Pfin(el.dur, el.p).play(tempoclock); nil});
				};
			};
		});
		if (this.internallist != []) {
			this.internalplayers[this.prInternalnewcounter.asSymbol] = Ppar(this.internallist).play(tempoclock);
		};
		^nil;
	}

	/*
	[method.stop]
	description = '''
	Force stops playing the timeline.
	'''
	[method.stop.returns]
	what = "nil"
	*/
	stop {
		this.internalplayers.keysValuesDo({
			| key, value |
			value.stop;
		});
		this.internalplayers = ();
		^nil;
	}

	/*
	[method.asView]
	description = '''
	returns a View containing the timeline which can be embedded in a Layout.
	'''
	[method.asView.returns]
	what = "a View"
	*/
	asView {
		^this.plotter.asView(this.internalplotlist);
	}

	/*
	[method.plot]
	description = '''
	creates a window and embeds the timeline into it and displays it
	'''
	[method.plot.returns]
	what = "a Window"
	*/
	plot {
		var w = Window.new(bounds:Rect(200,200,1200,300));
		w.layout_(VLayout(this.asView));
		w.front;
	}

	/*
	[method.prInternalnewcounter]
	description = '''
	generates a new unique counter value. Internal usage.
	'''
	[method.prInternalnewcounter.returns]
	what = "a new integer"
	*/
	prInternalnewcounter {
		var cnt = this.internalcounter;
		this.internalcounter = this.internalcounter + 1;
		^cnt;
	}

	/*
	[method.prInternalnewplotcounter]
	description = '''
	generates a new unique counter symbol. Internal usage.
	'''
	[method.prInternalnewplotcounter.returns]
	what = "a new symbol"
	*/
	prInternalnewplotcounter {
		var cnt = this.internalcounter;
		this.internalcounter = this.internalcounter + 1;
		^cnt.asSymbol;
	}
}

/*
[examples]
what = '''
(
var timeline = ScTimeline();
var pianoslow = Pbind();
var pianofast = Pbind();
var strings = Pbind();
var brass = Pbind();
var percussion = Pbind();
var newtempo = Pbind();
var gong = Pbind();
timeline.schedBeat(0, 60, pianoslow, "piano 1", Color.red);
timeline.schedBeat(90, 182, pianoslow, "piano 1", Color.red);
timeline.schedBeat(20, 182, pianofast, "piano 2", Color.red);
timeline.schedBeat(60, 140, strings, "strings", Color.blue);
timeline.schedBeat(90, 182, brass, "brass 1", Color.green);
timeline.schedBeat(130, 182, brass, "brass 2", Color.green);
timeline.schedBeat(160, 182, brass, "brass 3", Color.green);
timeline.schedBeat(90, 182, percussion, "percussion", Color.gray);
timeline.schedBeatNumber(176, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(177, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(178, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(179, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(180, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(181, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(182, 1, newtempo, "tempo", Color.black);
timeline.schedBeatNumber(183, 1, gong, "gong", Color.magenta);

// play the timeline
timeline.play; // the Pbinds are empty, so you won't hear anything...

// stop playing the timeline
timeline.stop;

// plotting: first way: just create new window with timeline
timeline.plot;

// plotting: second way: embed into your own ui
w = Window.new(bounds:Rect(200,200,1200,300));
w.layout_(VLayout(timeline.asView));
w.front;
)
'''
*/
