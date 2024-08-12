package net.minevn.minigames;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public class Messages {
	//region error msg
	public static String ERR_GENERAL = "Có lỗi xảy ra, vui lòng báo cho admin hoặc thử lại sau!";
	public static String ERR_NO_SPEAKERWORLD = "§cBạn đã hết loa thế giới, hãy tìm mua ở shop.";

	public static String ERR_SHOP_NO_POINTS = "§cBạn không có đủ xu để mua, dùng lệnh §e/napthe §cđể nạp thêm xu nhé!";
	public static String ERR_SHOP_NO_MONEY = "§cBạn không có đủ MG để mua, hãy chơi game để kiếm thêm MG nhé!";
	public static String ERR_LONG_PET_NAME = "§cTên pet không được dài hơn 32 kí tự.";
	public static String ERR_RIDE_NO_TICKET = "§cBạn không có vé vào khu này.";
	public static String ERR_RIDE_UNAVAILABLE = "§cTrò chơi này đã đóng cửa hoặc không tồn tại.";
	public static String ERR_RIDE_PLAYING = "§cTrò chơi này đã bắt đầu rồi, đợi một chút nhé.";
	public static String ERR_LONG_NICKNAME = "§cNickname không được dài hơn 32 kí tự.";
	public static String ERR_LONG_GUNNAME = "§cTên súng không được dài hơn 32 kí tự.";
	public static String ERR_NICKNAME_USED = "§cMỗi thẻ đổi tên chỉ được dùng một lần.";
	public static String ERR_GIFTCODE_NOT_VALID = "§cMã quà tặng không hợp lệ hoặc đã sử dụng.";
	public static String ERR_GIFTCODE_ONLY_ONCE = "§cBạn đã sử dụng mã quà này rồi, hãy nhường cho người khác nhé!";
	public static String ERR_GIFTCODE_USED = "§cMã quà tặng này đã được sử dụng.";
	public static String ERR_RENAME_GUN_ONLY = "§cThẻ này chỉ dùng để đổi tên súng.";
	public static String ERR_RENAME_GUN_ACTIVATED_ONLY = "§cBạn cần kích hoạt súng trước rồi mới đổi tên được.";

	public static String ERR_NUMBER_INTEGER_GTE0_FORMAT = "§cSố phải là số nguyên dương";
	public static String ERR_NUMBER_INTEGER_FORMAT = "§cSố phải là số nguyên";
	public static String ERR_NUMBER_CURRENCY_SHIPPING_EMPTY = "§cBạn phải có đính kèm để ra giá";
	public static String ERR_NUMBER_GTE_ITEM_AMOUNT = "§cSố không được vượt quá số lượng vật phẩm này";

	public static String ERR_MAIL_SEND_TO_SELF = "§cKhông thể gửi thư cho chính mình";
	public static String ERR_MAIL_NO_RECEIVER = "§cKhông tìm thấy người nhận";
	public static String ERR_MAIL_SEND_MULTI_RECEIVERS = "§cKhông thể gửi thư cho nhiều người cùng lúc";
	public static String ERR_MAIL_ATTACHMENT_FULL = "§cĐã đạt giới hạn số lượng vật phẩm đính kèm trong hộp thư";
	public static String ERR_MAIL_SEND_NO_RECEIVER = "§cNgười nhận %player% không tồn tại, không thể gửi thư";
	public static String ERR_MAIL_SEND_NO_POINTS = "§cBạn không đủ %points% Points để gửi thư.";
	public static String ERR_MAIL_SEND_CANNOT_SHIP = "§cKhông thể gửi thư có đính kèm %item%.";
	public static String ERR_MAIL_SEND_NO_PAID_ATTACHMENT = "§cKhông thể ra giá khi thư không có đính kèm";
	public static String ERR_MAIL_ATTACHMENT_NO_MONEY = "§cKhông đủ tiền để nhận đính kèm.";
	public static String ERR_MAIL_ATTACHMENT_NO_POINTS = "§cKhông đủ Points để nhận đính kèm.";
	public static String ERR_MAIL_SEND_NO_CONTENT = "§cKhông thể gửi thư trống.";
	//endregion

	// region monke
	public static String MONKE_EXP = "§cBạn không thể lên cấp và không được tính thành tích khi chơi phiên bản 1.8, hãy chơi phiên bản 1.12.2 trở lên nhé!";
	// endregion

	//region gui titles
	public static String GUI_TITLE_INVENTORY = "§2Kho vật phẩm";
	public static String GUI_TITLE_QUEST_CATEGORIES = "§2Nhiệm vụ";
	public static String GUI_TITLE_MAIL = "§2Hộp thư";
	public static String GUI_TITLE_MAIL_SENT = "§2Thư đã gửi";
	public static String GUI_TITLE_MAIL_COMPOSE = "§2Soạn thư";
	public static String GUI_TITLE_MAIL_VIEW = "§2Xem thư";
	//endregion

	//region gui buttons
	public static String GUI_BTN_CLOSE = "§cĐóng lại";
	public static String GUI_BTN_BACK = "§cTrở lại";
	public static String GUI_BTN_NEXT_PAGE = "§atrang kế tiếp >>";
	public static String GUI_BTN_PREV_PAGE = "§a<< trang trước";
	// -

	// Quest
	public static String GUI_BTN_QUEST_INFO_STATUS = "§bTình trạng: %status%";
	public static String GUI_BTN_QUEST_INFO_PROGRESS = "§bTiến độ: §a§l%done%/%required%";
	public static String GUI_BTN_QUEST_INFO_RESET_WAIT = "§bThời gian chờ: §e%timer%";
	public static String GUI_BTN_QUEST_INFO_REWARD = "§ePhần thưởng:";
	// -
	public static String GUI_BTN_QUEST_CLICK_TO_OBTAIN = "§6▸ §e[Click] §7§ođể nhận nhiệm vụ";
	public static String GUI_BTN_QUEST_CLICK_TO_CANCEL = "§6▸ §e[Shift-Click] §7§ođể hủy nhiệm vụ §4§o(-10 điểm)";
	public static String GUI_BTN_QUEST_UNOBTAINABLE = "§c§oKhông thể nhận thêm nhiệm vụ";
	// -
	public static String GUI_BTN_QUEST_STATUS_OBTAINABLE = "§d§lCHỜ TIẾP NHẬN";
	public static String GUI_BTN_QUEST_STATUS_DOING = "§e§lĐANG THỰC HIỆN";
	public static String GUI_BTN_QUEST_STATUS_FINISHED = "§a§lHOÀN THÀNH";
	// -
	//mail
	public static String GUI_BTN_MAIL_RECEIVERS = "§eNgười nhận";
	public static List<String> GUI_BTN_MAIL_RECEIVERS_DESC = Arrays.asList(
			"§7Thêm người nhận thư."
	);
	public static String GUI_BTN_MAIL_CONTENT = "§eNội dung thư";
	public static List<String> GUI_BTN_MAIL_CONTENT_DESC = Arrays.asList(
			"§f",
			"§7Tiêu đề:§r %title%",
			"§f",
			"§eNhấn để sửa nội dung");
	public static String GUI_BTN_MAIL_ATTACHMENT = "§eĐính kèm vật phẩm";
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_DESC = Arrays.asList(
			"§7Gửi kèm vật phẩm lấy",
			"§7từ kho đồ của bạn"
	);
	public static String GUI_BTN_MAIL_MONEY = "§eMG: %money%";
	public static List<String> GUI_BTN_MAIL_MONEY_DESC = Arrays.asList(
			"§8‣ §7Đặt số §adương§7 để gửi MG tới người nhận",
			"§8‣ §7Đặt số §câm§7 để ra giá cho vật phẩm đính kèm",
			"§8(Chỉ khi có vật phẩm đính kèm)",
			"§f",
			"§7Nhấn để sửa MG đính kèm"
	);
	public static String GUI_BTN_MAIL_POINTS = "§ePoints: %points%";
	public static List<String> GUI_BTN_MAIL_POINTS_DESC = Arrays.asList(
			"§8‣ §7Đặt số §adương§7 để gửi Points tới người nhận",
			"§8‣ §7Đặt số §câm§7 để ra giá cho vật phẩm đính kèm",
			"§8(Chỉ khi có vật phẩm đính kèm)",
			"§f",
			"§7Nhấn để sửa Points đính kèm"
	);
	public static String GUI_BTN_MAIL_SEND = "§eGửi thư";
	public static List<String> GUI_BTN_MAIL_SEND_DESC = Arrays.asList(
			"§7Phí gửi thư: §e%points%"
	);
	public static String GUI_BTN_MAIL_ATTACHMENT_RECEIVE = "§eNhận đính kèm";
	public static String GUI_BTN_MAIL_ATTACHMENT_CANT_RECEIVE = "§4✖ §cKhông thể nhận";
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_RECEIVE_DESC = Arrays.asList(
		"§7Nhận hết các vật phẩm",
		"§7hoặc MG, Points được gửi kèm"
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_RECEIVED_DESC = Arrays.asList(
		"§7Bạn đã nhận đính kèm",
		"§7thư này rồi."
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_NOT_AFFORD_DESC = Arrays.asList(
		"§7Bạn không đủ tiền",
		"§7để nhận đính kèm"
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_REVOKED_DESC = Arrays.asList(
		"§7Người gửi đã",
		"§7thu hồi đính kèm"
	);
	public static String GUI_BTN_MAIL_ATTACHMENT_RECEIVE_FEE = "§7Phí nhận đính kèm: ";
	public static String GUI_BTN_MAIL_PREVIEW_MONEY = "§e%money% MG";
	public static List<String> GUI_BTN_MAIL_PREVIEW_MONEY_DESC = Arrays.asList(
		"§7Bạn được gửi §e%money% MG"
	);
	public static List<String> GUI_BTN_MAIL_PREVIEW_MONEY_DESC_FEE = Arrays.asList(
		"§7Phí nhận đính kèm là §e%money% MG"
	);
	public static String GUI_BTN_MAIL_PREVIEW_POINTS = "§e%points% Points";
	public static List<String> GUI_BTN_MAIL_PREVIEW_POINTS_DESC = Arrays.asList(
		"§7Bạn được gửi §e%points% Points"
	);
	public static List<String> GUI_BTN_MAIL_PREVIEW_POINTS_DESC_FEE = Arrays.asList(
		"§7Phí nhận đính kèm là §e%points% Points"
	);


	public static String GUI_BTN_MAIL_ATTACHMENT_REVOKE = "§eThu hồi đính kèm";
	public static String GUI_BTN_MAIL_ATTACHMENT_CANT_REVOKE = "§4✖ §cKhông thể thu hồi";
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_REVOKE_DESC = Arrays.asList(
		"§7Thu hồi hết các vật phẩm",
		"§7hoặc MG, Points được gửi kèm"
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_REVOKE_MULTI_DESC = Arrays.asList(
		"§7Không thể thu hôi",
		"§7từ thư gửi cho nhiều người"
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_ALREADY_REVOKED_DESC = Arrays.asList(
		"§7Bạn đã thu hồi đính kèm",
		"§7từ thư này rồi."
	);
	public static List<String> GUI_BTN_MAIL_ATTACHMENT_REVOKE_RECEIVED_DESC = Arrays.asList(
		"§7Người nhận đã nhận đính kèm"
	);

	public static String GUI_BTN_MAIL_COMPOSE = "§eSoạn thư";
	public static List<String> GUI_BTN_MAIL_COMPOSE_DESC = Arrays.asList(
		"§7Soạn thư và gửi vật phẩm",
		"§7hoặc tiền đến người khác"
	);

	public static String GUI_BTN_MAIL_SENT = "§eThư đã gửi";
	public static List<String> GUI_BTN_MAIL_SENT_DESC = Arrays.asList(
		"§7Xem lại các thư đã gửi"
	);
	public static String GUI_BTN_MAIL = "§eHộp thư";
	public static List<String> GUI_BTN_MAIL_DESC = Arrays.asList(
		"§7Quay về hộp thư"
	);
	//endregion

	//region player item
	public static String PI_CATEGORY_GADGETS = "§3Gadgets";
	public static String PI_CATEGORY_HITSOUND = "§3Hiệu ứng sát thương";
	public static String PI_CATEGORY_OTHERS = "§3Linh tinh";
	public static String PI_CATEGORY_ROOM = "§3Hiệu ứng phòng chờ";
	public static String PI_CATEGORY_SWORD = "§3Skin kiếm";
	public static String PI_CATEGORY_BOW = "&3Skin cung";
	public static String PI_CATEGORY_COSTUME = "§3Trang phục";
	public static String PI_CATEGORY_TRAIL = "§3Hiệu ứng mũi tên";
	public static String PI_CATEGORY_CHATCOLORS = "§3Màu chat";
	public static String PI_CATEGORY_MSGUN = "§3Súng";
	public static String PI_CATEGORY_MSTOMB = "§3Bia đá";
	public static String PI_CATEGORY_MSKNIFE = "§3Cận chiến";
	public static String PI_CATEGORY_CASE = "§3Rương báu vật";
	public static String PI_CATEGORY_CASEKEY = "§3Chìa khóa mở rương";
	public static String PI_CATEGORY_MVPANTHEM = "§3Giai điệu chiến thắng";
	public static String PI_CATEGORY_TOOLS = "§3Bổ trợ";
	public static String PI_CATEGORY_TOMB = "§3Bia đá";
	public static String PI_CATEGORY_PET = "§3Pet";
	public static String PI_CATEGORY_MINIATURE = "§3Figure";
	public static String PI_CATEGORY_RIDE_TICKET = "§3Vé khu vui chơi";
	public static String PI_CATEGORY_NICKNAME = "§3Thẻ đổi tên";
	public static String PI_CATEGORY_GUNNAME = "§3Thẻ đổi tên súng";

	public static String PI_EXPIRE_DATE = "§eNgày hết hạn: %date%";
	public static String PI_DURATION = "§eHạn sử dụng: %days% ngày";
	public static String PI_ATTACHMENT_SHIPPING_COST = "§ePhí vận chuyển: %cost%";
	public static String PI_CANNOT_SHIP = "§cKhông thể đính kèm";

	public static String PI_CLICK_TO_CHANGE_AMOUNT = "§eShift-Click để thay đổi số lượng";
	public static String PI_CLICK_TO_LISTEN = "§6▶ §eClick để nghe thử";
	public static String PI_CLICK_TO_USE = "§6▶ §eShift-Click để sử dụng";
	public static String PI_CLICK_TO_UNUSE = "§6▶ §eShift-Click để hủy sử dụng";
	public static String PI_CLICK_TO_ACTIVATE = "§6▸ §eShift-Click để kích hoạt";
	public static String PI_CLICKR_TO_RENAME = "§e▸ [Click phải] để đổi tên";
	public static String PI_CLICK_TO_RENAME = "§e▸ [Click] để đổi tên";
	public static String PI_CLICK_TO_OPEN = "§6▸ §eClick để mở";
	public static String PI_CLICK_TO_ATTACH_MAIL = "§aClick để thêm đính kèm";
	public static String PI_CLICK_TO_DETACH_MAIL = "§cClick để gỡ đính kèm";

	public static String PI_ITEM_CASEKEY = "§eChìa khóa rương %case%";
	public static String PI_ITEM_CASEBUNDLE = "§eRương và chìa khóa %case%";
	public static String PI_SPEAKERWORLD = "§e§lLoa thế giới";
	public static String PI_NICKNAME = "§eThẻ đổi tên %preset%";
	public static String PI_GUNNAME = "§eThẻ đổi tên súng %preset%";
	public static String PI_MS_HUMANSHIELD = "§e§lBùa miễn dịch";
	public static String PI_MS_DEADLYSHOT = "§e§lPhát bắn chí mạng";
	public static String PI_MS_SPEEDBOOST = "§e§lTăng tốc";
	public static String PI_MS_ZOMBIEGRENADE = "§e§lLựu đạn Zombie";
	public static String PI_MS_CREEPERGRENADE = "§e§lLựu đạn §2§lCreeper";
	public static String PI_MS_PVE_MEDIC = "§e§lDụng cụ y tế";
	public static String PI_MS_PVE_MEDIC_LARGE = "§e§lDụng cụ y tế (lớn)";
	public static String PI_MS_PVE_RETRY = "§e§lĐồng xu tái sinh";

	public static List<String> PI_SPEAKERWORLD_DESC = Arrays.asList(
			"§aGửi tin nhắn cho toàn server",
			"§aCách dùng: §b/loa <nội dung>");
	public static List<String> PI_MAIN_DESC = Arrays.asList(
			"§aDanh mục chính của cửa hàng",
			"§anơi trưng bày vật phẩm nổi bật");
	public static List<String> PI_WEAPON_DESC = Arrays.asList(
			"§aThay đổi skin kiếm",
			"§a(cần có gói tài nguyên)");
	public static List<String> PI_COSTUME_DESC = Arrays.asList(
			"§aNón",
			"§a(cần có gói tài nguyên)");
	public static List<String> PI_EFFECT_DESC = Arrays.asList(
			"§aTạo hiệu ứng xung quanh mũi tên",
			"§akhi bạn bắn cung");
	public static List<String> PI_ITEM_DESC = Arrays.asList(
			"§aVật phẩm chung");
	public static List<String> PI_TOMB_DESC = Arrays.asList(
			"§aBia mộ khi bạn hẻo");
	public static List<String> PI_CRATES_DESC = Arrays.asList(
			"§aQuay ra những vật phẩm",
			"§ahiếm và độc lạ");
	public static List<String> PI_PET_DESC = Arrays.asList(
			"§aPet bên cạnh bạn tại sảnh"
	);
	public static List<String> PI_MINIATURE_DESC = Arrays.asList(
			"§aFigure bên cạnh bạn tại sảnh"
	);
	public static List<String> PI_RIDE_TICKET_DESC = Arrays.asList(
			"§aVé chơi các trò chơi như",
			"§avòng đu quay, vòng ngựa gỗ, ..."
	);
	public static List<String> PI_NICKNAME_DESC = Arrays.asList(
			"§aTùy chỉnh tên của bạn"
	);

	public static List<String> PI_GUNNAME_DESC = Arrays.asList(
			"§aTùy chỉnh tên súng của bạn,",
			"§akéo thả vào súng để đổi tên"
	);

	public static List<String> PI_MS_HUMANSHIELD_DESC = Arrays.asList(
			"§eSử dụng trong: §c§lZombie Hero",
			"§eMiễn nhiễm với zombie",
			"§eở phát cào đầu tiên");
	public static List<String> PI_MS_DEADLYSHOT_DESC = Arrays.asList(
			"§eSử dụng trong: §c§lZombie Hero",
			"§eGây sát thương headshot lên zombie",
			"§eở mọi phát bắn trong §a7 giây");
	public static List<String> PI_MS_SPEEDBOOST_DESC = Arrays.asList(
			"§eSử dụng trong: §c§lZombie Hero",
			"§eTăng tốc độ di chuyển",
			"§etrong §a10 giây");
	public static List<String> PI_MS_ZOMBIEGRENADE_DESC = Arrays.asList(
			"§eSử dụng trong: §c§lZombie Hero");
	public static List<String> PI_MS_CREEPERGRENADE_DESC = Arrays.asList(
			"§eSử dụng trong: §c§lZombie Hero");
	public static List<String> PI_MS_PVE_MEDIC_DESC = Arrays.asList(
			"§eHồi 30% sinh lực",
			"§eSử dụng trong: §b§lPhó Bản");
	public static List<String> PI_MS_PVE_MEDIC_LARGE_DESC = Arrays.asList(
			"§eHồi 100% sinh lực",
			"§eSử dụng trong: §b§lPhó Bản");
	public static List<String> PI_MS_PVE_RETRY_DESC = Arrays.asList(
			"§eDùng để tái sinh",
			"§ekhi qua màn thất bại",
			"§eSử dụng trong: §b§lPhó Bản");
	//endregion

	//region Shop
	public static String SHOP_CLICK_TO_PREVIEW = "§6▶ §e§lChuột phải §eđể xem thử";
	public static String SHOP_CLICK_TO_BUY = "§6▶ §e§lChuột trái §eđể mua";

	public static String SHOP_ITEM_LIMIT = "§3Số lượng có hạn";
	public static String SHOP_ITEM_LAST = "§3Số lượng có hạn";
	public static String SHOP_OUT_OF_STOCK = "§cHết hàng";

	public static List<String> SHOP_PRICE_SALE_LORE = Arrays.asList(
			"§f﹃ Giá gốc: §7%price% %price_type% §b§o(%unit%)",
			"§f §f §f Giảm sốc: %color%§l %price_discount% %price_type% §c(-%sale%%) §f﹄");
	public static List<String> SHOP_PRICE_LORE = List.of(
			"§f﹃ Giá: %color%§l %price%%price_type% §b§o(%unit%) §f﹄");
	//endregion

	// region messages
	public static String MSG_EXP_RECEIVE = "§bBạn nhận được §a%exp% §bđiểm kinh nghiệm";
	public static String MSG_EXP_RECEIVE_NOT_ENOUGH_PLAYERS = "§fTrong phòng không có đủ người để tính điểm kinh nghiệm.";
	public static String MSG_LEVEL_UP = "§f§lChúc mừng bạn đã lên cấp §f%icon%§b§l%level%";
	public static String MSG_HOUR = "giờ";
	public static String MSG_MINUTE = "phút";
	public static String MSG_QUEST_NO_QUEST = "§cKhông có nhiệm vụ nào để thực hiện.";
	public static String MSG_QUEST_ANNOUNCE = "§aBạn đã hoàn tất §e§l%done%/%required% §anhiệm vụ: §f%name%";
	public static String MSG_QUEST_AWARD = "§aBạn nhận được §e%item% §atừ nhiệm vụ";
	public static String MSG_ITEM_AWARD = "§aBạn nhận được §e%item%§a, hãy kiểm tra trong §e§lKho đồ";
	public static String MSG_PET_RENAME_INPUT = "§7Nhập tên Pet mong muốn vào khung chat";
	public static String MSG_PET_RENAME_SUCCESS = "§eĐổi tên pet thành công.";
	public static List<String> MSG_PET_RENAME_RULES = Arrays.asList(
			"§f",
			"§4⚠ §cQuy định khi đặt tên Pet",
			"§f §6⌬ §7Không sử dụng từ ngữ thô tục",
			"§f §6⌬ §7Không đặt tên người khác làm tên Pet",
			"§f §6⌬ §7Không đặt tên nhằm mục đích mỉa mai, xúc phạm người khác",
			"§f",
			"§7§oMọi hành vi vi phạm sẽ bị xử phạt theo quy định!"
	);
	public static String MSG_NICK_RENAME_INPUT = "§7Nhập nickname mong muốn vào khung chat";
	public static String MSG_NICK_RENAME_SUCCESS = "§eĐổi nickname thành công.";
	public static List<String> MSG_NICK_RENAME_RULES = Arrays.asList(
			"§f",
			"§4⚠ §cQuy định khi đặt nickname",
			"§f §6⌬ §7Không sử dụng từ ngữ thô tục",
			"§f §6⌬ §7Không đặt tên giả mạo người khác",
			"§f §6⌬ §7Không đặt tên nhằm mục đích mỉa mai, xúc phạm người khác",
			"§f",
			"§7§oMọi hành vi vi phạm sẽ bị xử phạt theo quy định!"
	);

	public static String MSG_GUN_RENAME_INPUT = "§7Nhập tên súng mong muốn vào khung chat";
	public static String MSG_GUN_RENAME_SUCCESS = "§eĐổi tên súng thành công.";
	public static List<String> MSG_GUN_RENAME_RULES = Arrays.asList(
			"§f",
			"§4⚠ §cQuy định khi đặt tên súng",
			"§f §6⌬ §7Không sử dụng từ ngữ thô tục",
			"§f §6⌬ §7Không đặt tên nhằm mục đích mỉa mai, xúc phạm người khác",
			"§f",
			"§7§oMọi hành vi vi phạm sẽ bị xử phạt theo quy định!"
	);

	public static String MSG_MAIL_TYPE_ITEM_AMOUNT = "§7Nhập số lượng vật phẩm muốn đính kèm vào khung chat. (Tối đa %max_amount%)";
	public static String MSG_SET_ITEM_AMOUNT = "§aĐã chỉnh số lượng của vật phẩm này thành %amount%";
	public static String MSG_MAIL_SENT = "§aĐã gửi thư thành công.";
	public static String MSG_MAIL_TYPE_RECEIVERS = "§7Nhập tên người nhận vào khung chat.";
	public static String MSG_MAIL_RECEIVERS_SET = "§aĐã cập nhật người nhận.";
	public static String MSG_MAIL_TYPE_CONTENT = "§aClick vào quyển sách để nhập nội dung và tiêu đề thư.";
	public static String MSG_MAIL_CONTENT_SET = "§aĐã cập nhật nội dung thư.";
	public static String MSG_MAIL_TYPE_MONEY = "§7Nhập số tiền muốn đính kèm vào khung chat.";
	public static String MSG_MAIL_MONEY_SET = "§aĐã cập nhật số tiền đính kèm thành %money%.";
	public static String MSG_MAIL_TYPE_POINTS = "§7Nhập số point muốn đính kèm vào khung chat.";
	public static String MSG_MAIL_POINTS_SET = "§aĐã cập nhật số point đính kèm thành %point%.";
	public static String MSG_MAIL_ATTACHMENT_RECEIVED = "§aBạn đã nhận đính kèm rồi.";
	public static String MSG_MAIL_ATTACHMENT_REVOKE_RECEIVED = "§aNgười nhận đã nhận đính kèm rồi.";
	public static String MSG_MAIL_UNREAD_NOTICE = "§aBạn có %amount% thư chưa đọc.";
	// endregion

	//region economy
	public static String ECO_GIVE = "§6+ %amount% MG";
	public static String ECO_TAKE = "§6- %amount% MG";
	//endregion

	// region mail
	public static List<String> MAIL_DESC = Arrays.asList(
			"§7Người gửi:§e %sender%",
			"§7Thời gian:§e %date%",
			"§7",
			"%containing%",
			"§e ▸ %money% MG",
			"§e ▸ %points% Point",
			"§e ▸ %attachment% vật phẩm",
			"§7",
			"§a%attachmentStatus%",
			"§7",
			"§e▸ [Chuột trái] để đọc thư",
			"§e▸ [Shift-Chuột trái] để mở đính kèm",
			"§e▸ [Shift-Chuột phải] để xóa thư"
	);
	public static String MAIL_DESC_CONTAINING = "§7Trong phong bì có: ";
	public static String MAIL_SENT_DESC_TIME = "§7Thời gian:§e %date%";
	public static String MAIL_SENT_DESC_OPEN_READ = "§e▸ [Chuột trái] để đọc thư";
	public static String MAIL_SENT_DESC_OPEN_ATTACHMENT = "§e▸ [Shift-Chuột trái] để mở đính kèm";
	public static String MAIL_SENT_DESC_RECEIVERS = "§7Người nhận";
	public static String MAIL_DEFAULT_TITLE = "Không có tiêu đề";
	public static String MAIL_ATTACHMENT_RECEIVED = "Đã nhận đính kèm";
	public static String MAIL_ATTACHMENT_REVOKED = "§4Đính kèm đã bị thu hồi";
	public static String MAIL_ATTACHMENT_NOT_RECEIVED = "Chưa nhận đính kèm";
	public static String MAIL_UNREAD = "Chưa đọc";
	public static String MAIL_READ = "Đã đọc";

	public static String MAIL_MONEY_RETURN_TITLE = "Nhận phí đính kèm";
	public static String MAIL_MONEY_RETURN_MESSAGE = """
		Bạn nhận được thanh toán từ thư số %mailid%.
		Mở đính kèm để nhận.
		""".stripIndent();
	// endregion

	// region relative times
	public static String TIME_JUSTNOW = "vừa xong";
	public static String TIME_MINUTES = "%time% phút trước";
	public static String TIME_HOURS = "%time% giờ trước";
	public static String TIME_YESTERDAY = "hôm qua";
	public static String TIME_DAYS = "%time% ngày trước";
	public static String TIME_TOMOROW = "ngày mai";
	public static String TIME_AFTER_DAYS = "sau %time% ngày";
	public static String TIME_AFTER_HOURS = "sau %time% giờ";
	public static String TIME_AFTER_MINUTES = "sau %time% phút";
	public static String TIME_RIGHTNOW = "ngay bây giờ";
	// endregion
}
