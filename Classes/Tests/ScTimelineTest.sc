ScTimelineTest1 : UnitTest {
	test_check_classname {
		var result = ScTimeline.new;
		this.assert(result.class == ScTimeline);
	}
}


ScTimelineTester {
	*new {
		^super.new.init();
	}

	init {
		ScTimelineTest1.run;
	}
}
