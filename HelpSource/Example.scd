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

// first way: just create new window with timeline
timeline.plot;

// second way: embed into your own ui
w = Window.new(bounds:Rect(200,200,1200,300));
w.layout_(VLayout(timeline.asView));
w.front;

)