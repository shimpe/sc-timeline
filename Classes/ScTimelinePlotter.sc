/*
[general]
title = "ScTimelinePlotter"
summary = "an object to plot the contents of an ScTimeline"
categories = "Patterns"
related = "Classes/ScTimeline"
description = '''
ScTimelinePlotter provides an object to draw patterns in time.
'''
*/
ScTimelinePlotter {
	/*
	[method.v]
	description='''
	v contains a UserView. Internal usage only.
	'''
	[method.v.returns]
	what = "a UserView"
	*/
	var <>v;
	/*
	[method.lane_margin_x]
	description='''
	an integer indicating a small gap between adjacent patterns in a given lane. Internal usage.
	'''
	[method.lane_margin_x.returns]
	what = "an integer"
	*/
	var <>lane_margin_x;
	/*
	[method.lane_margin_y]
	description='''
	an integer indicating a small gap between overlapping patterns in a two adjacent lanes. Internal usage.
	'''
	[method.lane_margin_y.returns]
	what = "an integer"
	*/
	var <>lane_margin_y;
	/*
	[method.instrument_name_width]
	description='''
	width required to display the pattern names. Internal usage.
	'''
	[method.instrument_name_width.returns]
	what = "an integer"
	*/
	var <>instrument_name_width;
	/*
	[method.time_grid]
	description='''
	Time grid division.
	'''
	[method.time_grid.returns]
	what = "an integer"
	*/
	var <>time_grid;
	/*
	[method.time_grid_color]
	description='''
	Time grid color.
	'''
	[method.time_grid_color.returns]
	what = "a Color"
	*/
	var <>time_grid_color;
	/*
	[method.time_grid_string_color]
	description='''
	Time grid string color (color used to draw time values).
	'''
	[method.time_grid_string_color.returns]
	what = "a Color"
	*/
	var <>time_grid_string_color;
	/*
	[method.lane_grid]
	description='''
	Lane grid. Set to nil to not draw the lanes.
	'''
	[method.lane_grid.returns]
	what = "either something or nil"
	*/
	var <>lane_grid;
	/*
	[method.lane_grid_color]
	description='''
	Lane grid color.
	'''
	[method.lane_grid_color.returns]
	what = "a Color"
	*/
	var <>lane_grid_color;
	/*
	[method.alpha]
	description='''
	Pattern transparency. A float (0-1), where 1 is opaque and 0 is fully transparent. Default:0.5.
	'''
	[method.alpha.returns]
	what = "a float"
	*/
	var <>alpha;
	/*
	[method.draw_time_strings]
	description='''
	A boolean indicating if time strings should be added to the plot.
	'''
	[method.draw_time_strings.returns]
	what = "a boolean"
	*/
	var <>draw_time_strings;

	/*
	[classmethod.new]
	description = "New creates a new ScTimelinePlotter"
	[classmethod.new.returns]
	what = "a new ScTimelinePlotter"
	*/
	*new {
		^super.new.init();
	}

	/*
	[method.init]
	description = '''
	initialize internal data structures
	'''
	*/
	init {
		this.lane_margin_x = 1;
		this.lane_margin_y = 3;
		this.instrument_name_width = 80;
		this.time_grid = nil;
		this.time_grid_color = Color.gray;
		this.time_grid_string_color = Color.black;
		this.lane_grid = 1;
		this.lane_grid_color = Color.gray;
		this.alpha = 0.5;
		this.draw_time_strings = true;
	}

	/*
	[method.drawTimeGrid]
	description = '''
	method that draws the time grid if time_grid was not set to nil
	'''
	[method.drawTimeGrid.args]
	time_span = "time range for which a grid must be drawn"
	totalwidth = "width in which the time grid must fit"
	totalheight = "height in which the time grid must fit"
	*/
	drawTimeGrid {
		| time_span, totalwidth, totalheight |
		// overlay time grid if needed
		if (this.time_grid.notNil) {
			var no_of_lines = (time_span / this.time_grid).round(1).asInteger;
			no_of_lines.do({
				| idx |
				var x = (idx*this.time_grid).linlin(0, time_span, this.instrument_name_width, totalwidth);
				var y1 = 0;
				var y2 = totalheight;
				Pen.alpha_(1.0);
				Pen.strokeColor_(this.time_grid_color);
				Pen.line(x@y1, x@y2);
				Pen.fillStroke;

				if (this.draw_time_strings) {
					Pen.fillColor_(this.time_grid_string_color);
					Pen.stringAtPoint((idx*this.time_grid).asString, (x+3)@(y2-18));
					Pen.fillStroke;
				};
			});
		};
	}

	/*
	[method.drawLaneGrid]
	description = '''
	method that draws the lanes in which the patterns are visualized if lane_grid was not set to nil
	'''
	[method.drawLaneGrid.args]
	no_of_bins = "number of lanes to draw"
	totalwidth = "width in which the lane grid must fit"
	totalheight = "height in which the lane grid must fit"
	*/
	drawLaneGrid {
		| no_of_bins, totalwidth, totalheight |
		if (this.lane_grid.notNil) {
			var no_of_lines = no_of_bins + 1;
			no_of_lines.do({
				|idx|
				var x1 = this.instrument_name_width;
				var x2 = totalwidth;
				var y = idx.linlin(0, no_of_bins, 0, totalheight);
				Pen.alpha_(1.0);
				Pen.strokeColor_(this.lane_grid_color);
				Pen.line(x1@y, x2@y);
				Pen.fillStroke;
			});
		};
	}

	/*
	[method.drawTracksAndLabels]
	description = '''
	method that draws the patterns and instrument labels
	'''
	[method.drawTracksAndLabels.args]
	internalplotlist = "pattern information received from the ScTimeline"
	totalwidth = "width in which the patterns must fit"
	totalheight = "height in which the patterns must fit"
	bin_to_idx = "mapping of a lane name to a lane index"
	time_span = "time over which the plotting must be spread"
	no_of_bins = "number of lanes to draw"
	*/
	drawTracksAndLabels {
		| internalplotlist, totalwidth, totalheight, bin_to_idx, time_span, no_of_bins |
		var txts_drawn = Set[];
		internalplotlist.do({
			| el |
			var bin = bin_to_idx[el[\name]];
			var binheight = totalheight / no_of_bins;
			var upper = bin.linlin(0, no_of_bins, 0, totalheight);
			var left = el[\start].linlin(0, time_span, this.instrument_name_width, totalwidth);
			var lower = (bin+1).linlin(0, no_of_bins, 0, totalheight);
			var right = 0;
			var text = el[\name];
			var block = nil;
			var color = nil;

			if ((el[\type] == \beat) || (el[\type] == \time)) {
				right = el[\stop].linlin(0, time_span, this.instrument_name_width, totalwidth);
			} {
				right = el[\start].linlin(0, time_span, this.instrument_name_width, totalwidth) + 30;
			};

			if (el[\color].notNil) {
				color = el[\color];
			} {
				color = Color.blue;
			};
			Pen.fillColor_(color);
			Pen.alpha_(1.0);

			// draw track label if needed
			if (txts_drawn.includes(text).not) {
				txts_drawn = txts_drawn.add(text);
				Pen.stringCenteredIn(text.asString, Rect(0, upper, this.instrument_name_width, lower-upper));
				Pen.fillStroke;
			};

			// draw track
			block = Rect(left + lane_margin_x, upper + lane_margin_y, right - left - (2*lane_margin_x), lower - upper - (2*lane_margin_y));
			if (el[\type] == \number) {
				Pen.fillColor_(color);
				Pen.addOval(block);
				Pen.alpha_(this.alpha);
				Pen.fillStroke;
				Pen.fillColor_(Color.white);
				Pen.alpha_(1.0);
				Pen.stringCenteredIn(el[\stop].asString, block);
				Pen.fillStroke;
			} {
				Pen.alpha_(this.alpha);
				Pen.fillRect(block);
				Pen.fillStroke;
			};
		});
	}

	/*
	[method.analyzeBins]
	description = '''
	method that analyzes how many bins and which bins need to be drawn from the ScTimeline internalplotlist information.
	'''
	[method.analyzeBins.args]
	internalplotlist = "pattern information received from the ScTimeline"
	[method.analyzeBins.returns]
	what = 'an event containing (\bintoidx : bin_to_idx, \span: time_span) where bintoidx is a map from lane name to lane number and time span is total span of all event\'s durations'
	*/
	analyzeBins {
		| internalplotlist |
		var time_span = 0;
		var bins = Set[];
		var bin_to_idx = ();
		var bincnt = 0;
		internalplotlist.do({
			| el, idx |
			if (bins.includes(el[\name]).not) {
				bins = bins.add(el[\name]);
				bin_to_idx[el[\name]] = bincnt;
				bincnt = bincnt + 1;
				if ((el[\type] == \beat) || (el[\type] == \time)) {
					if (el[\stop] > time_span) {
						time_span = el[\stop];
					}
				} {
					if (el[\start] > time_span) {
						time_span = el[\start];
					}
				};
				time_span = time_span + 1;
			};
		});
		if (this.time_grid.isNil) {
			this.time_grid = (time_span/10.0).asInteger;
			if (time_span > 10) {
				this.time_grid = this.time_grid.round(5);
			} {
				if (time_span > 1) {
					this.time_grid = this.time_grid.round(1);
				}
			};
		};
		^(\bintoidx : bin_to_idx, \span: time_span);
	}

	/*
	[method.asView]
	description = '''
	returns a View with the plot that can be embedded in a View
	'''
	[method.asView.args]
	internalplotlist = "pattern information received from the ScTimeline"
	[method.asView.returns]
	what = 'a View'
	*/
	asView {
		| internalplotlist |
		this.v = UserView().background_(Color.white);
		this.v.animate = false;
		this.v.drawFunc_({
			| uview |
			var totalwidth = uview.bounds.width;
			var totalheight = uview.bounds.height;
			var binanalysis = this.analyzeBins(internalplotlist);
			var bin_to_idx = binanalysis[\bintoidx];
			var time_span = binanalysis[\span];
			var no_of_bins = bin_to_idx.size();
			if (no_of_bins == 0) {
				^nil;
			};
			this.drawTracksAndLabels(internalplotlist, totalwidth, totalheight, bin_to_idx, time_span, no_of_bins);
			this.drawTimeGrid(time_span, totalwidth, totalheight);
			this.drawLaneGrid(no_of_bins, totalwidth, totalheight);

		});
		^this.v;
	}
}
