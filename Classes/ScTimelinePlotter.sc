ScTimelinePlotter {
	var <>v;
	var <>lane_margin_x;
	var <>lane_margin_y;
	var <>text_width;
	var <>time_grid;
	var <>time_grid_color;
	var <>lane_grid;
	var <>lane_grid_color;
	var <>alpha;

	*new {
		^super.new.init();
	}

	init {
		this.lane_margin_x = 1;
		this.lane_margin_y = 3;
		this.text_width = 80;
		this.time_grid = 1;
		this.time_grid_color = Color.gray;
		this.lane_grid = 1;
		this.lane_grid_color = Color.gray;
		this.alpha = 0.5;
	}

	drawTimeGrid {
		| time_span, totalwidth, totalheight |
		// overlay time grid if needed
		if (this.time_grid.notNil) {
			var no_of_lines = (time_span / this.time_grid).asInteger;
			no_of_lines.do({
				| idx |
				var x = idx.linlin(0, no_of_lines, this.text_width, totalwidth);
				var y1 = 0;
				var y2 = totalheight;
				Pen.alpha_(1.0);
				Pen.strokeColor_(this.time_grid_color);
				Pen.line(x@y1, x@y2);
				Pen.fillStroke;
			});
		};
	}

	drawLaneGrid {
		| no_of_bins, totalwidth, totalheight |
		if (this.lane_grid.notNil) {
			var no_of_lines = no_of_bins + 1;
			no_of_lines.do({
				|idx|
				var x1 = 0;
				var x2 = totalwidth;
				var y = idx.linlin(0, no_of_bins, 0, totalheight);
				Pen.alpha_(1.0);
				Pen.strokeColor_(this.lane_grid_color);
				Pen.line(x1@y, x2@y);
				Pen.fillStroke;
			});
		};
	}

	drawTracksAndLabels {
		| internalplotlist, totalwidth, totalheight, bin_to_idx, time_span, no_of_bins |
		var txts_drawn = Set[];
		internalplotlist.do({
				| el |
				var bin = bin_to_idx[el[\name]];
				var binheight = totalheight / no_of_bins;
				var upper = bin.linlin(0, no_of_bins, 0, totalheight);
				var left = el[\start].linlin(0, time_span, this.text_width, totalwidth);
				var lower = (bin+1).linlin(0, no_of_bins, 0, totalheight);
				var right = 0;
				var text = el[\name];
				var block = nil;
				var color = nil;

				if ((el[\type] == \beat) || (el[\type] == \time)) {
					right = el[\stop].linlin(0, time_span, this.text_width, totalwidth);
				} {
					right = el[\start].linlin(0, time_span, this.text_width, totalwidth) + 30;
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
					Pen.stringCenteredIn(text.asString, Rect(0, upper, this.text_width, lower-upper));
					Pen.fillStroke;
				};

				// draw track
				block = Rect(left + lane_margin_x, upper + lane_margin_y, right - left - lane_margin_x, lower - upper - lane_margin_y);
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

	asView {
		| internalplotlist |
		this.v = UserView().background_(Color.white);
		this.v.animate = false;
		this.v.drawFunc_({
			| uview |
			var totalwidth = uview.bounds.width;
			var totalheight = uview.bounds.height;
			var bins = Set[];
			var bins_sorted = [];
			var no_of_bins;
			var time_span = 0;
			var bin_to_idx = ();
			var bincnt = 0;
			internalplotlist.do({
				| el, idx |
				if (bins.includes(el[\name]).not) {
					bins = bins.add(el[\name]);
					bins_sorted = bins_sorted.add(el[\name]);
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
					time_span = time_span + 0.5;
				};
			});
			no_of_bins = bins_sorted.size();

			if (no_of_bins == 0) {
				^nil;
			};

			this.drawTracksAndLabels(internalplotlist, totalwidth, totalheight, bin_to_idx, time_span, no_of_bins);
			this.drawTimeGrid(time_span, totalwidth, totalheight);
			this.drawLaneGrid(no_of_bins, totalwidth, totalheight);			// overlay lane grid if needed

		});
		^this.v;
	}

}