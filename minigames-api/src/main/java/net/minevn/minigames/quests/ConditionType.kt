package net.minevn.minigames.quests

enum class ConditionType {
	/**
	 * Roll từ 0 đến 10000, nhỏ hơn giá trị truyền vào thì pass condition
	 *
	 * Thường dùng cho drop
	 */
	ROLL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	PLAYERS_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	IN_MODES {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	IN_MAPS {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	PLAYER_HEALTH_LT {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	PLAYER_IS_JUMPING {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	IS_ALIVE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	/**
	 * Số người tối thiểu trong các mode solo, duo, trio, squad cách nhau dấu |
	 *
	 * Ví dụ nếu khai báo 3|5|8|10 thì lần lượt solo, duo, trio, squad cần tối thiểu 3 người, 5 người, 8 và 10 người.
	 *
	 * Có thể dùng condition này để chỉnh quest chỉ áp dụng cho một số mode, vd chỉ áp dụng cho solo/duo thì tăng
	 * số người yêu cầu của trio & squad lên 99
	 */
	BW_PLAYERS_GTE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	BW_FINAL_KILL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	BW_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	BW_SOLO_OR_DUO {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	BW_TRIO_OR_SQUAD {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_BASE_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_HEADSHOT {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_BLOCKBANG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_GRENADE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_KNIFE_OR_GRENADE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_EXPLOSIVE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_FIRE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_SNIPER {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_LAST_AMMO {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_RIFLE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_SHOTGUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_SMG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_MG {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_WITH_PISTOL {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_KILL_VICTIM_HOLDING_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MS_PLAYER_HAVE_GUN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MM_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MM_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MM_ROLE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MM_INNOCENT_REMAIN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	MM_TIME_REMAIN {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	VD_KILL_WITH_KNIFE {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	VD_KILL_WITH_BOW {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	TL_TOP {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	},

	BB_POINTS {
		override fun check(attempt: QuestAttempt, condition: Condition): Boolean = false
	};

	abstract fun check(attempt: QuestAttempt, condition: Condition): Boolean
}