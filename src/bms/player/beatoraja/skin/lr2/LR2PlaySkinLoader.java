package bms.player.beatoraja.skin.lr2;

import static bms.player.beatoraja.skin.SkinProperty.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntArray;

import bms.model.Mode;
import bms.player.beatoraja.Config;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.play.PlaySkin;
import bms.player.beatoraja.play.SkinBGA;
import bms.player.beatoraja.play.SkinGauge;
import bms.player.beatoraja.play.SkinJudge;
import bms.player.beatoraja.play.SkinNote;
import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinBPMGraph;
import bms.player.beatoraja.skin.SkinDestinationSize;
import bms.player.beatoraja.skin.SkinHeader;
import bms.player.beatoraja.skin.SkinImage;
import bms.player.beatoraja.skin.SkinNoteDistributionGraph;
import bms.player.beatoraja.skin.SkinNumber;
import bms.player.beatoraja.skin.SkinSource;
import bms.player.beatoraja.skin.SkinSourceImage;
import bms.player.beatoraja.skin.SkinTimingVisualizer;
import bms.player.beatoraja.skin.SkinType;

/**
 * LR2�깤�꺃�궎�궧�궘�꺍�꺆�꺖���꺖
 *
 * @author exch
 */
public class LR2PlaySkinLoader extends LR2SkinCSVLoader<PlaySkin> {

	private SkinBGA bga;

	private SkinNote lanerender;

	private Rectangle[] playerr;

	private Mode mode;

	private SkinSource[] note = new SkinSource[8];
	private SkinSource[] lnstart = new SkinSource[8];
	private SkinSource[] lnend = new SkinSource[8];
	private SkinSource[] lnbody = new SkinSource[8];
	private SkinSource[] lnbodya = new SkinSource[8];
	private SkinSource[] hcnstart = new SkinSource[8];
	private SkinSource[] hcnend = new SkinSource[8];
	private SkinSource[] hcnbody = new SkinSource[8];
	private SkinSource[] hcnbodya = new SkinSource[8];
	private SkinSource[] hcnbodyd = new SkinSource[8];
	private SkinSource[] hcnbodyr = new SkinSource[8];
	private SkinSource[] mine = new SkinSource[8];
	private Rectangle[] laner = new Rectangle[8];
	private float[] scale = new float[8];
	private SkinGauge gauger = null;
	private SkinImage line;
	private SkinImage[] lines = new SkinImage[8];
	private String[][] linevalues = new String[2][];

	private SkinJudge[] judge = new SkinJudge[3];

	private int groovex = 0;
	private int groovey = 0;

	private SkinType type;

	final float srcw;
	final float srch;
	final float dstw;
	final float dsth;


	private Rectangle gauge = new Rectangle();
	private SkinNoteDistributionGraph noteobj;
	private SkinBPMGraph bpmgraphobj;
	private SkinTimingVisualizer timingobj;

	public LR2PlaySkinLoader(final SkinType type, final Resolution src, final Config c) {
		super(src, c);

		this.type = type;
		srcw = src.width;
		srch = src.height;
		dstw = dst.width;
		dsth = dst.height;

		addCommandWord(new CommandWord("CLOSE") {
			@Override
			public void execute(String[] str) {
				skin.setClose(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("PLAYSTART") {
			@Override
			public void execute(String[] str) {
				skin.setPlaystart(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("LOADSTART") {
			@Override
			public void execute(String[] str) {
				skin.setLoadstart(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("LOADEND") {
			@Override
			public void execute(String[] str) {
				skin.setLoadend(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("FINISHMARGIN") {
			@Override
			//STATE_FINISHED�걢�굢�깢�궒�꺖�깋�궋�궑�깉�굮�뼀冶뗣걲�굥�겲�겎�겗�깯�꺖�궦�꺍(ms)
			public void execute(String[] str) {
				skin.setFinishMargin(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("JUDGETIMER") {
			@Override
			public void execute(String[] str) {
				skin.setJudgetimer(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("SRC_BGA") {
			@Override
			public void execute(String[] str) {
				bga = new SkinBGA(c.getBgaExpand());
				skin.add(bga);
			}
		});
		addCommandWord(new CommandWord("DST_BGA") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				if (bga != null) {
					SkinDestinationSize dstSize = new SkinDestinationSize(gauge.x, gauge.y, gauge.width, gauge.height);
					skin.setDestination(bga, 0, dstSize,
							values[7], values[8], values[9], values[10], values[11], values[12], values[13], values[14],
							values[15], values[16], values[17], values[18], values[19], values[20], readOffset(str, 21));
				}
			}
		});
		addCommandWord(new CommandWord("SRC_LINE") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				TextureRegion[] images = getSourceImage(values);
				if (images != null) {
					SkinImage li = new SkinImage(images, values[10], values[9]);
					lines[values[1]] = li;
					// System.out.println("Object Added - " +
					// (part.getTiming()));
				}
			}
		});
		addCommandWord(new CommandWord("DST_LINE") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				if (lines[values[1]] != null) {
					if (values[5] < 0) {
						values[3] += values[5];
						values[5] = -values[5];
					}
					if (values[6] < 0) {
						values[4] += values[6];
						values[6] = -values[6];
					}
					SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth / srch,
							values[5] * dstw / srcw, values[6] * dsth / srch);
					lines[values[1]].setDestination(values[2], dstSize, values[7], values[8], values[9],
							values[10], values[11], values[12], values[13], values[14], values[15], values[16],
							values[17], values[18], values[19], values[20], readOffset(str, 21, new int[]{OFFSET_LIFT}));
					if(playerr[values[1] % 2] != null) {
						playerr[values[1] % 2] = new Rectangle(values[3] * dstw / srcw,
								dsth - (values[4] + values[6]) * dsth / srch, values[5] * dstw / srcw,
								(values[4] + values[6]) * dsth / srch);
					}
					linevalues[values[1] % 2] = str.clone();
				}
			}
		});
		addCommandWord(new CommandWord("SRC_NOTE") {
			@Override
			public void execute(String[] str) {
				addNote(str, note, true);
			}
		});
		addCommandWord(new CommandWord("SRC_LN_END") {
			@Override
			public void execute(String[] str) {
				addNote(str, lnend, true);
			}
		});
		addCommandWord(new CommandWord("SRC_LN_START") {
			@Override
			public void execute(String[] str) {
				addNote(str, lnstart, true);
			}
		});
		addCommandWord(new CommandWord("SRC_LN_BODY") {
			@Override
			public void execute(String[] str) {
				addNote(str, lnbody, false);
				addNote(str, lnbodya, true);
			}
		});
		addCommandWord(new CommandWord("SRC_LN_BODY_INACTIVE") {
			@Override
			public void execute(String[] str) {
				addNote(str, lnbody, true);
			}
		});
		addCommandWord(new CommandWord("SRC_LN_BODY_ACTIVE") {
			@Override
			public void execute(String[] str) {
				addNote(str, lnbodya, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_END") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnend, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_START") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnstart, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_BODY") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnbody, false);
				addNote(str, hcnbodya, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_BODY_INACTIVE") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnbody, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_BODY_ACTIVE") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnbodya, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_DAMAGE") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnbodyd, true);
			}
		});
		addCommandWord(new CommandWord("SRC_HCN_REACTIVE") {
			@Override
			public void execute(String[] str) {
				addNote(str, hcnbodyr, true);
			}
		});

		addCommandWord(new CommandWord("SRC_MINE") {
			@Override
			public void execute(String[] str) {
				addNote(str, mine, true);
			}
		});

		addCommandWord(new CommandWord("DST_NOTE") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				int lane = Integer.parseInt(str[1]);
				if (lane % 10 == 0) {
					lane = mode.scratchKey.length > (lane / 10) ? mode.scratchKey[(lane / 10)] : -1;
				} else {
					final int offset = (lane / 10) * (laner.length / playerr.length);
					lane = (lane > 10) ? lane - 11 : lane - 1;
					if (lane >= (laner.length - mode.scratchKey.length) / playerr.length) {
						lane = -1;
					} else {
						lane += offset;
					}
				}
				if(lane < 0) {
					return;
				}
				if (laner[lane] == null) {
					laner[lane] = new Rectangle(values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth / srch,
							values[5] * dstw / srcw, (values[4] + values[6]) * dsth / srch);
					scale[lane] = values[6] * dsth / srch;
				}
				if (lanerender == null) {
					if(hcnend[0] == null) {
						hcnend = lnend;
					}
					if(hcnstart[0] == null) {
						hcnstart = lnstart;
					}
					if(hcnbody[0] == null) {
						hcnbody = lnbody;
					}
					if(hcnbodya[0] == null) {
						hcnbodya = lnbodya;
					}
					if(hcnbodyd[0] == null) {
						hcnbodyd = hcnbody;
					}
					if(hcnbodyr[0] == null) {
						hcnbodyr = hcnbodya;
					}
					final SkinSource[][] lns = new SkinSource[][]{ lnend, lnstart, lnbodya, lnbody, hcnend,
							hcnstart, hcnbodya, hcnbody, hcnbodyr, hcnbodyd };
					final SkinSource[][] lnss = new SkinSource[lnstart.length][10];
					for(int i = 0;i < 10;i++) {
						for(int j = 0;j < lnstart.length;j++) {
							lnss[j][i] = lns[i][j];
						}
					}
					lanerender = new SkinNote(note, lnss, mine);
					lanerender.setOffsetID(readOffset(str, 21, new int[]{OFFSET_NOTES_1P}));
					skin.add(lanerender);
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOTE2") {
			@Override
			//PMS�겗誤뗩�껁걮POOR�셽�겗�깕�꺖�깉�걣�맼�걾�굥轢붷눣�겗易덂ㅁ�궧�겗y佯㎪쮽
			public void execute(String[] str) {
				lanerender.setDstNote2((int) (dsth - (Integer.parseInt(str[1]) * dsth / srch + scale[0])));
			}
		});

		addCommandWord(new CommandWord("DST_NOTE_EXPANSION_RATE") {
			@Override
			//PMS�겗�꺁�궨�깲�겓�릦�굩�걵�걼�깕�꺖�깉�겗�떋鸚㎯겗��鸚㎪떋鸚㎫럤(%) �똷,h��
			public void execute(String[] str) {
				skin.setNoteExpansionRate(new int[]{Integer.parseInt(str[1]),Integer.parseInt(str[2])});
			}
		});

		addCommandWord(new CommandWord("SRC_NOWJUDGE_1P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				TextureRegion[] images = getSourceImage(values);
				if (images != null) {
					if (judge[0] == null) {
						judge[0] = new SkinJudge(0, (values[11] != 1));
						skin.add(judge[0]);
					}
					judge[0].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinImage(images, values[10], values[9]);
					// System.out.println("Nowjudge Added - " + (5 -
					// values[1]));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWJUDGE_1P") {

			private boolean detail = false;

			@Override
			public void execute(String[] str) {
				if (judge[0] != null && judge[0].getJudge()[Integer.parseInt(str[1]) <= 5 ? (5 - Integer.parseInt(str[1])) : Integer.parseInt(str[1])] != null) {
					try {
						int[] values = parseInt(str);
						if (values[5] < 0) {
							values[3] += values[5];
							values[5] = -values[5];
						}
						if (values[6] < 0) {
							values[4] += values[6];
							values[6] = -values[6];
						}
						SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw,
								dsth - (values[4] + values[6]) * dsth / srch, values[5] * dstw / srcw,
								values[6] * dsth / srch);
						judge[0].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]].setDestination(values[2], dstSize
								, values[7], values[8], values[9], values[10], values[11],
								values[12], values[13], values[14], values[15], values[16], values[17], values[18],
								values[19], values[20], readOffset(str, 21, new int[]{OFFSET_JUDGE_1P, OFFSET_LIFT}));

						if (!detail) {
							detail = true;
							addJudgeDetail(skin, values, srcw, dstw, srch, dsth, 0);
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_NOWJUDGE_2P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				TextureRegion[] images = getSourceImage(values);
				if (images != null) {
					if (judge[1] == null) {
						judge[1] = new SkinJudge(1, (values[11] != 1));
						skin.add(judge[1]);
					}
					judge[1].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinImage(images, values[10], values[9]);
					// System.out.println("Nowjudge Added - " + (5 -
					// values[1]));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWJUDGE_2P") {

			private boolean detail = false;

			@Override
			public void execute(String[] str) {
				if (judge[1] != null && judge[1].getJudge()[Integer.parseInt(str[1]) <= 5 ? (5 - Integer.parseInt(str[1])) : Integer.parseInt(str[1])] != null) {
					try {
						int[] values = parseInt(str);
						if (values[5] < 0) {
							values[3] += values[5];
							values[5] = -values[5];
						}
						if (values[6] < 0) {
							values[4] += values[6];
							values[6] = -values[6];
						}
						SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw,
								dsth - (values[4] + values[6]) * dsth / srch, values[5] * dstw / srcw,
								values[6] * dsth / srch);
						judge[1].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]].setDestination(values[2], dstSize
								, values[7], values[8], values[9], values[10], values[11],
								values[12], values[13], values[14], values[15], values[16], values[17], values[18],
								values[19], values[20], readOffset(str, 21, new int[]{OFFSET_JUDGE_2P, OFFSET_LIFT}));

						if (!detail) {
							detail = true;
							addJudgeDetail(skin, values, srcw, dstw, srch, dsth, 1);
						}
					} catch(Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_NOWJUDGE_3P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				TextureRegion[] images = getSourceImage(values);
				if (images != null) {
					if (judge[2] == null) {
						judge[2] = new SkinJudge(2, (values[11] != 1));
						skin.add(judge[2]);
					}
					judge[2].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinImage(images, values[10], values[9]);
					// System.out.println("Nowjudge Added - " + (5 -
					// values[1]));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWJUDGE_3P") {

			private boolean detail = false;

			@Override
			public void execute(String[] str) {
				if (judge[2] != null && judge[2].getJudge()[Integer.parseInt(str[1]) <= 5 ? (5 - Integer.parseInt(str[1])) : Integer.parseInt(str[1])] != null) {
					try {
						int[] values = parseInt(str);
						if (values[5] < 0) {
							values[3] += values[5];
							values[5] = -values[5];
						}
						if (values[6] < 0) {
							values[4] += values[6];
							values[6] = -values[6];
						}
						SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw,
								dsth - (values[4] + values[6]) * dsth / srch, values[5] * dstw / srcw,
								values[6] * dsth / srch);
						judge[2].getJudge()[values[1] <= 5  ? (5 - values[1]) : values[1]].setDestination(values[2], dstSize
								, values[7], values[8], values[9], values[10], values[11],
								values[12], values[13], values[14], values[15], values[16], values[17], values[18],
								values[19], values[20], readOffset(str, 21, new int[]{OFFSET_JUDGE_3P, OFFSET_LIFT}));

						if (!detail) {
							detail = true;
							addJudgeDetail(skin, values, srcw, dstw, srch, dsth, 2);
						}

					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_NOWCOMBO_1P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				int divx = values[7];
				if (divx <= 0) {
					divx = 1;
				}
				int divy = values[8];
				if (divy <= 0) {
					divy = 1;
				}
				TextureRegion[] simages = getSourceImage(values);
				if (simages != null) {
					TextureRegion[][] images = new TextureRegion[divy][divx];
					for (int i = 0; i < divx; i++) {
						for (int j = 0; j < divy; j++) {
							images[j][i] = simages[j * divx + i];
						}
					}

					judge[0].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinNumber(images, values[10], values[9], values[13],
							images.length > 10 ? 2 : 0, values[11]);
					judge[0].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].setAlign(values[12] == 1 ?  2 : values[12]);
					// System.out.println("Number Added - " +
					// (num.getId()));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWCOMBO_1P") {
			@Override
			public void execute(String[] str) {
				setDstNowCombo(0, str, readOffset(str, 21, new int[]{OFFSET_JUDGE_1P, OFFSET_LIFT}));
			}
		});

		addCommandWord(new CommandWord("SRC_NOWCOMBO_2P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				int divx = values[7];
				if (divx <= 0) {
					divx = 1;
				}
				int divy = values[8];
				if (divy <= 0) {
					divy = 1;
				}
				TextureRegion[] simages = getSourceImage(values);
				if (simages != null) {
					TextureRegion[][] images = new TextureRegion[divy][divx];
					for (int i = 0; i < divx; i++) {
						for (int j = 0; j < divy; j++) {
							images[j][i] = simages[j * divx + i];
						}
					}

					judge[1].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinNumber(images, values[10], values[9], values[13],
							images.length > 10 ? 2 : 0, values[11]);
					judge[1].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].setAlign(values[12] == 1 ?  2 : values[12]);
					// System.out.println("Number Added - " +
					// (num.getId()));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWCOMBO_2P") {
			@Override
			public void execute(String[] str) {
				setDstNowCombo(1, str, readOffset(str, 21, new int[]{OFFSET_JUDGE_2P, OFFSET_LIFT}));
			}
		});

		addCommandWord(new CommandWord("SRC_NOWCOMBO_3P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				int divx = values[7];
				if (divx <= 0) {
					divx = 1;
				}
				int divy = values[8];
				if (divy <= 0) {
					divy = 1;
				}
				TextureRegion[] simages = getSourceImage(values);
				if (simages != null) {
					TextureRegion[][] images = new TextureRegion[divy][divx];
					for (int i = 0; i < divx; i++) {
						for (int j = 0; j < divy; j++) {
							images[j][i] = simages[j * divx + i];
						}
					}

					judge[2].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]] = new SkinNumber(images, values[10], values[9], values[13],
							images.length > 10 ? 2 : 0, values[11]);
					judge[2].getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].setAlign(values[12] == 1 ?  2 : values[12]);
					// System.out.println("Number Added - " +
					// (num.getId()));
				}
			}
		});

		addCommandWord(new CommandWord("DST_NOWCOMBO_3P") {
			@Override
			public void execute(String[] str) {
				setDstNowCombo(2, str, readOffset(str, 21, new int[]{OFFSET_JUDGE_3P, OFFSET_LIFT}));
			}
		});

		addCommandWord(new CommandWord("SRC_JUDGELINE") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				TextureRegion[] images = getSourceImage(values);
				if (images != null) {
					line = new SkinImage(images, values[10], values[9]);
					// System.out.println("Object Added - " +
					// (part.getTiming()));
				}
				if (line != null) {
					skin.add(line);
				}
			}
		});

		addCommandWord(new CommandWord("DST_JUDGELINE") {
			@Override
			public void execute(String[] str) {
				if (line != null) {
					int[] values = parseInt(str);
					if (values[5] < 0) {
						values[3] += values[5];
						values[5] = -values[5];
					}
					if (values[6] < 0) {
						values[4] += values[6];
						values[6] = -values[6];
					}
					SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw,
							dsth - (values[4] + values[6]) * dsth / srch, values[5] * dstw / srcw,
							values[6] * dsth / srch);
					line.setDestination(values[2], dstSize, values[7], values[8], values[9], values[10], values[11],
							values[12], values[13], values[14], values[15], values[16], values[17], values[18],
							values[19], values[20], readOffset(str, 21, new int[]{OFFSET_LIFT}));
                }
			}
		});

		addCommandWord(new CommandWord("SRC_GROOVEGAUGE") {
			@Override
			public void execute(String[] str) {
				gauger = null;
				int[] values = parseInt(str);
				if (values[2] < imagelist.size() && imagelist.get(values[2]) != null) {
					int playside = values[1];
					int divx = values[7];
					if (divx <= 0) {
						divx = 1;
					}
					int divy = values[8];
					if (divy <= 0) {
						divy = 1;
					}
					TextureRegion[][] gauge;
					if(values[14] == 3 && divx * divy % 6 == 0) {
						//�궋�깑�깳�꺖�궥�깾�꺍�궭�궎�깤�걣PMS�뵪�삇譯끹궋�깑�깳�꺖�궥�깾�꺍�겗�졃�릦 烏②덮�곮〃渶묆�곮즽壅ㅳ�곮즽渶묆�곭쇇�뀎烏②덮�곭쇇�뀎烏①톿�겗�젂�겓src�늽�돯
						gauge = new TextureRegion[(divx * divy) / 6][12];
						final int w = values[5];
						final int h = values[6];
						for (int x = 0; x < divx; x++) {
							for (int y = 0; y < divy; y++) {
								if ((y * divx + x) / 6 < gauge.length) {
									if((y * divx + x) % 6 < 4) {
										gauge[(y * divx + x) / 6][(y * divx + x) % 6] = new TextureRegion(
												(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
												values[4] + h * y / divy, w / divx, h / divy);
										gauge[(y * divx + x) / 6][(y * divx + x) % 6 + 4] = new TextureRegion(
												(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
												values[4] + h * y / divy, w / divx, h / divy);
									} else {
										gauge[(y * divx + x) / 6][(y * divx + x) % 6 + 4] = new TextureRegion(
												(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
												values[4] + h * y / divy, w / divx, h / divy);
										gauge[(y * divx + x) / 6][(y * divx + x) % 6 + 6] = new TextureRegion(
												(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
												values[4] + h * y / divy, w / divx, h / divy);
									}
								}
							}
						}
					} else {
						gauge = new TextureRegion[(divx * divy) / 4][8];
						final int w = values[5];
						final int h = values[6];
						for (int x = 0; x < divx; x++) {
							for (int y = 0; y < divy; y++) {
								if ((y * divx + x) / 4 < gauge.length) {
									gauge[(y * divx + x) / 4][(y * divx + x) % 4] = new TextureRegion(
											(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
											values[4] + h * y / divy, w / divx, h / divy);
									gauge[(y * divx + x) / 4][(y * divx + x) % 4 + 4] = new TextureRegion(
											(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
											values[4] + h * y / divy, w / divx, h / divy);
								}
							}
						}
					}
					groovex = values[11];
					groovey = values[12];
					if (gauger == null) {
						if(values[13] == 0) {
							gauger = new SkinGauge(gauge, values[10], values[9], mode == Mode.POPN_9K ? 24 : 50, 0, mode == Mode.POPN_9K ? 0 : 3, 33);
						} else {
							gauger = new SkinGauge(gauge, values[10], values[9], values[13], values[14], values[15], values[16]);
						}

						skin.add(gauger);
					}
				}
			}
		});
		addCommandWord(new CommandWord("SRC_GROOVEGAUGE_EX") {
			//JSON�궧�궘�꺍�겏�릪壤℡폀�뎵 烏②덮�곮〃渶묆�곮즽壅ㅳ�곮즽渶묆�갋X烏②덮�갋X烏①톿�갋X獒뤺덮�갋X獒뤹톿�겗�젂�겓src�늽�돯
			@Override
			public void execute(String[] str) {
				gauger = null;
				int[] values = parseInt(str);
				if (values[2] < imagelist.size() && imagelist.get(values[2]) != null) {
					int playside = values[1];
					int divx = values[7];
					if (divx <= 0) {
						divx = 1;
					}
					int divy = values[8];
					if (divy <= 0) {
						divy = 1;
					}
					TextureRegion[][] gauge;
					if(values[14] == 3 && divx * divy % 12 == 0) {
						//�궋�깑�깳�꺖�궥�깾�꺍�궭�궎�깤�걣PMS�뵪�삇譯끹궋�깑�깳�꺖�궥�깾�꺍�겗�졃�릦 烏②덮�곮〃渶묆�곮즽壅ㅳ�곮즽渶묆�갋X烏②덮�갋X烏①톿�갋X獒뤺덮�갋X獒뤹톿�곭쇇�뀎烏②덮�곭쇇�뀎烏①톿�곭쇇�뀎EX烏②덮�곭쇇�뀎EX烏①톿�겗�젂�겓src�늽�돯
						gauge = new TextureRegion[(divx * divy) / 12][12];
						final int w = values[5];
						final int h = values[6];
						for (int x = 0; x < divx; x++) {
							for (int y = 0; y < divy; y++) {
								if ((y * divx + x) / 12 < gauge.length) {
										gauge[(y * divx + x) / 12][(y * divx + x) % 12] = new TextureRegion(
												(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
												values[4] + h * y / divy, w / divx, h / divy);
								}
							}
						}
					} else {
						gauge = new TextureRegion[(divx * divy) / 8][8];
						final int w = values[5];
						final int h = values[6];
						for (int x = 0; x < divx; x++) {
							for (int y = 0; y < divy; y++) {
								if ((y * divx + x) / 8 < gauge.length) {
									gauge[(y * divx + x) / 8][(y * divx + x) % 8] = new TextureRegion(
											(Texture) imagelist.get(values[2]), values[3] + w * x / divx,
											values[4] + h * y / divy, w / divx, h / divy);
								}
							}
						}
					}
					groovex = values[11];
					groovey = values[12];
					if (gauger == null) {
						if(values[13] == 0) {
							gauger = new SkinGauge(gauge, values[10], values[9], mode == Mode.POPN_9K ? 24 : 50, 0, mode == Mode.POPN_9K ? 0 : 3, 33);
						} else {
							gauger = new SkinGauge(gauge, values[10], values[9], values[13], values[14], values[15], values[16]);
						}

						skin.add(gauger);
					}
				}
			}
		});
		addCommandWord(new CommandWord("DST_GROOVEGAUGE") {
			@Override
			public void execute(String[] str) {
				if (gauger != null) {
					float width = (Math.abs(groovex) >= 1) ? (groovex * 50 * dstw / srcw)
							: (Integer.parseInt(str[5]) * dstw / srcw);
					float height = (Math.abs(groovey) >= 1) ? (groovey * 50 * dsth / srch)
							: (Integer.parseInt(str[6]) * dsth / srch);
					float x = Integer.parseInt(str[3]) * dstw / srcw - (groovex < 0 ? groovex * dstw / srcw : 0);
					float y = dsth - Integer.parseInt(str[4]) * dsth / srch - height;
					int[] values = parseInt(str);
					SkinDestinationSize dstSize = new SkinDestinationSize(x, y, width, height);
					gauger.setDestination(values[2], dstSize, values[7],
							values[8], values[9], values[10], values[11], values[12], values[13], values[14],
							values[15], values[16], values[17], values[18], values[19], values[20], readOffset(str, 21));
				}
			}
		});
		addCommandWord(new CommandWord("SRC_NOTECHART_1P") {
			//#SRC_NOTECHART_1P,(index),(gr),(x),(y),(w),(h),(div_x),(div_y),(cycle),(timer),field_w,field_h,(start),(end),delay,backTexOff,orderReverse,noGap
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				noteobj = new SkinNoteDistributionGraph(values[1], values[15], values[16], values[17], values[18]);
				gauge = new Rectangle(0, 0, values[11], values[12]);
				skin.add(noteobj);
			}
		});
		addCommandWord(new CommandWord("DST_NOTECHART_1P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				gauge.x = values[3];
				gauge.y = src.height - values[4];
				SkinDestinationSize dstSize = new SkinDestinationSize(gauge.x, gauge.y, gauge.width, gauge.height);
				skin.setDestination(noteobj, values[2], dstSize, values[7], values[8],
						values[9], values[10], values[11], values[12], values[13], values[14], values[15],
						values[16], values[17], values[18], values[19], values[20], readOffset(str, 21));
			}
		});

		addCommandWord(new CommandWord("SRC_BPMCHART") {
			//#SRC_BPMCHART, field_w, field_h, delay, lineWidth, mainBPMColor, minBPMColor, maxBPMColor, otherBPMColor, stopLineColor, transitionLineColor
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				bpmgraphobj = new SkinBPMGraph(values[3], values[4], str[5], str[6], str[7], str[8], str[9], str[10]);
				gauge = new Rectangle(0, 0, values[1], values[2]);
				skin.add(bpmgraphobj);
			}
		});
		addCommandWord(new CommandWord("DST_BPMCHART") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				gauge.x = values[3];
				gauge.y = src.height - values[4];
				SkinDestinationSize dstSize = new SkinDestinationSize(gauge.x, gauge.y, gauge.width, gauge.height);
				skin.setDestination(bpmgraphobj, values[2], dstSize, values[7], values[8],
						values[9], values[10], values[11], values[12], values[13], values[14], values[15],
						values[16], values[17], values[18], values[19], values[20], readOffset(str, 21));
			}
		});
		addCommandWord(new CommandWord("SRC_TIMING_1P") {
			//#SRC_NOTECHART_1P,(index),(gr),(x),(y),(w),(h),(div_x),(div_y),(cycle),(timer),field_w,(field_h),(start),(end),drawCenter,drawDecay,(),()
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				timingobj = new SkinTimingVisualizer(values[11], values[15], values[16]);
				gauge = new Rectangle(0, 0, values[11], values[12]);
				skin.add(timingobj);
			}
		});
		addCommandWord(new CommandWord("DST_TIMING_1P") {
			@Override
			public void execute(String[] str) {
				int[] values = parseInt(str);
				gauge.x = values[3];
				gauge.y = src.height - values[4];
				SkinDestinationSize dstSize = new SkinDestinationSize(gauge.x, gauge.y, gauge.width, gauge.height);
				skin.setDestination(timingobj, values[2], dstSize, values[7], values[8],
						values[9], values[10], values[11], values[12], values[13], values[14], values[15],
						values[16], values[17], values[18], values[19], values[20], readOffset(str, 21));
			}
		});
	}

	private void setDstNowCombo(int index, String[] str, int[] offset) {
		final SkinJudge sj = judge[index];
		if (sj != null && sj.getJudgeCount()[Integer.parseInt(str[1]) <= 5 ? (5 - Integer.parseInt(str[1])) : Integer.parseInt(str[1])] != null) {
			int[] values = parseInt(str);
			sj.getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].setRelative(true);
			float x = values[3];
			if(sj.getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].getAlign() == 2) {
				x -= sj.getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].getKeta() * values[5] / 2;
			}
			SkinDestinationSize dstSize = new SkinDestinationSize( x * dstw / srcw,
					-values[4] * dsth / srch, values[5] * dstw / srcw, values[6] * dsth / srch);
			sj.getJudgeCount()[values[1] <= 5  ? (5 - values[1]) : values[1]].setDestination(values[2],dstSize, values[7],
					values[8], values[9], values[10], values[11], values[12], values[13], values[14],
					values[15], values[16], values[17], values[18], values[19], values[20], offset);
		}
	}

	private void addNote(String[] str, SkinSource[] note, boolean animation) {
		int[] values = parseInt(str);
		int lane = values[1];
		if (lane % 10 == 0) {
			lane = mode.scratchKey.length > (lane / 10) ? mode.scratchKey[(lane / 10)] : -1;
		} else {
			final int offset = (lane / 10) * (laner.length / playerr.length);
			lane = (lane > 10) ? lane - 11 : lane - 1;
			if (lane >= (laner.length - mode.scratchKey.length) / playerr.length) {
				lane = -1;
			} else {
				lane += offset;
			}
		}
		if(lane < 0) {
			return;
		}
		if (lane < note.length && note[lane] == null) {
			TextureRegion[] images = getSourceImage(values);
			if (images != null) {
				note[lane] = new SkinSourceImage(images, animation ? values[10] : 0, animation ? values[9] : 0);
			}
		}
	}

	private static void addJudgeDetail(Skin skin, int[] values, float srcw, float dstw, float srch, float dsth, int side) {
		Texture tex = new Texture("skin/default/judgedetail.png");

		final float dw = dstw / 1280f;
		final float dh = dsth / 720f;

		final int[] JUDGE_TIMER = { TIMER_JUDGE_1P, TIMER_JUDGE_2P, TIMER_JUDGE_3P };
		final int[] OPTION_EARLY = { OPTION_1P_EARLY, OPTION_2P_EARLY, OPTION_3P_EARLY };
		final int[] OPTION_LATE = { OPTION_1P_LATE, OPTION_2P_LATE, OPTION_3P_LATE };
		final int[] VALUE_JUDGE_DURATION = { VALUE_JUDGE_1P_DURATION, VALUE_JUDGE_2P_DURATION, VALUE_JUDGE_3P_DURATION };
		final int[] OPTION_PERFECT = { OPTION_1P_PERFECT, OPTION_2P_PERFECT, OPTION_3P_PERFECT };
		final int[] OFFSET_JUDGE = { OFFSET_JUDGE_1P, OFFSET_JUDGE_2P, OFFSET_JUDGE_3P };
		final int[] OFFSET_JUDGE_DETAIL = { OFFSET_JUDGEDETAIL_1P, OFFSET_JUDGEDETAIL_2P, OFFSET_JUDGEDETAIL_3P };
		SkinDestinationSize dstSize = new SkinDestinationSize((values[3] + values[5] / 2) * dstw / srcw,
				dsth - (values[4] - 5) * dsth / srch, 40 * dw, 16 * dh);
		
		SkinImage early = new SkinImage(new TextureRegion(tex, 0, 0, 50,20));
		early.setDestination(0, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1998, 0, OPTION_EARLY[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		early.setDestination(500, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1998, 0, OPTION_EARLY[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		skin.add(early);
		SkinImage late = new SkinImage(new TextureRegion(tex, 50, 0, 50,20));
		late.setDestination(0, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1998, 0, OPTION_LATE[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		late.setDestination(500, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1998, 0, OPTION_LATE[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		skin.add(late);

		TextureRegion[][] images = TextureRegion.split(tex, 10, 20);
		SkinNumber num = new SkinNumber(new TextureRegion[][] { images[1] },
				new TextureRegion[][] { images[2] }, 0, 0, 4, 0, VALUE_JUDGE_DURATION[side]);
		num.setAlign(values[12]);
		
		num.setDestination(0, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1999, 0, OPTION_PERFECT[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		num.setDestination(500, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1999, 0, OPTION_PERFECT[side], new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		skin.add(num);
		SkinNumber num2 = new SkinNumber(new TextureRegion[][] { images[3] },
				new TextureRegion[][] { images[4] }, 0, 0, 4, 0, VALUE_JUDGE_DURATION[side]);
		num2.setAlign(values[12]);
		num2.setDestination(0, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1999, 0, -(OPTION_PERFECT[side]), new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		num2.setDestination(500, dstSize, 0, 255,
				255, 255, 255, 0, 0, 0, 0, -1, JUDGE_TIMER[side], 1999, 0, -(OPTION_PERFECT[side]), new int[]{OFFSET_JUDGE_DETAIL[side], OFFSET_LIFT});
		skin.add(num2);
	}

	public PlaySkin loadSkin(File f, MainState player, SkinHeader header, Map<Integer, Boolean> option,
			Map property) throws IOException {
		mode = type.getMode();
		note = new SkinSource[mode.key];
		lnstart = new SkinSource[mode.key];
		lnend = new SkinSource[mode.key];
		lnbody = new SkinSource[mode.key];
		lnbodya = new SkinSource[mode.key];
		hcnstart = new SkinSource[mode.key];
		hcnend = new SkinSource[mode.key];
		hcnbody = new SkinSource[mode.key];
		hcnbodya = new SkinSource[mode.key];
		hcnbodyd = new SkinSource[mode.key];
		hcnbodyr = new SkinSource[mode.key];
		mine = new SkinSource[mode.key];
		laner = new Rectangle[mode.key];
		scale = new float[mode.key];

		playerr = new Rectangle[mode.player];
		for(int i = 0;i < playerr.length;i++) {
			playerr[i] = new Rectangle();
		}

		this.loadSkin(new PlaySkin(src, dst), f, player, header, option, property);

		lanerender.setLaneRegion(laner, scale, skin);

		SkinImage[] skinline = new SkinImage[lines[0] != null ? (lines[1] != null ? 2 : 1) : 0];
		for(int i = 0;i < skinline.length;i++) {
			skinline[i] = lines[i];
		}
		skin.setLine(skinline);
		SkinImage[] skintime = new SkinImage[skinline.length];
		for(int i = 0;i < skintime.length;i++) {
			if(lines[i + 6] == null && lines[i] != null) {
				makeDefaultLines(i + 6, 1, 64, 192, 192);
			}
			skintime[i] = lines[i + 6];
		}
		skin.setTimeLine(skintime);

		SkinImage[] skinbpm = new SkinImage[skinline.length];
		for(int i = 0;i < skinbpm.length;i++) {
			if(lines[i + 2] == null && lines[i] != null) {
				makeDefaultLines(i + 2, 2, 0, 192, 0);
			}
			skinbpm[i] = lines[i + 2];
		}
		skin.setBPMLine(skinbpm);

		SkinImage[] skinstop = new SkinImage[skinline.length];
		for(int i = 0;i < skinstop.length;i++) {
			if(lines[i + 4] == null && lines[i] != null) {
				makeDefaultLines(i + 4, 2, 192, 192, 0);
			}
			skinstop[i] = lines[i + 4];
		}
		skin.setStopLine(skinstop);

		int judge_reg = 1;
		for(int i = 1 ; i < judge.length ; i++) {
			if(judge[i] != null) judge_reg++;
			else break;
		}
		skin.setJudgeregion(judge_reg);
		skin.setLaneRegion(laner);
		skin.setLaneGroupRegion(playerr);

		return skin;
	}

	private void makeDefaultLines(int index, int h, int r, int g, int b) {
		Texture tex = new Texture("skin/default/system.png");
		int[] values = parseInt(linevalues[index % 2]);
		SkinImage li = new SkinImage(new TextureRegion(tex, 0, 0, 1,1));
		lines[index] = li;
		SkinDestinationSize dstSize = new SkinDestinationSize(values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth / srch,
				values[5] * dstw / srcw, values[6] * dsth / srch * h);
		lines[index].setDestination(values[2], dstSize, values[7], 255, r, g,
				b, values[12], values[13], values[14], values[15], values[16],
				values[17], values[18], values[19], values[20], readOffset(linevalues[index % 2], 21, new int[]{OFFSET_LIFT}));
	}
}
