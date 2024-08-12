package net.minevn.minigames.cases;

import net.minevn.guiapi.GuiInventory;
import net.minevn.guiapi.GuiItemStack;
import net.minevn.minigames.Minigames;
import net.minevn.minigames.PlayerData;
import net.minevn.minigames.Utils;
import net.minevn.minigames.award.PlayerAward;
import net.minevn.minigames.items.types.CaseKeyPI;
import net.minevn.minigames.items.types.CasePI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CaseGUI extends GuiInventory {

	private Case _case;
	private Player viewer;
	private Minigames main;

	public CaseGUI(Case _case, Player viewer) {
		super(36, _case.getName());
		this._case = _case;
		this.viewer = viewer;
		main = Minigames.getInstance();
		drawBorder();
		viewer.openInventory(getInventory());
		drawPreview();
	}

	public Case getCurrentCase() {
		return _case;
	}

	private void drawBorder() {
		for (int i = 27; i < 36; i++) {
			setItem(i, new GuiItemStack(Material.GRAY_STAINED_GLASS_PANE, "§f"));
		}
	}

	private void drawOpenBorder() {
		for (int i = 0; i < 36; i++) {
			setItem(i, new GuiItemStack(Material.GRAY_STAINED_GLASS_PANE, "§f"));
		}
		for (int i = 10; i < 17; i++) {
			setItem(i, new GuiItemStack(Material.WHITE_STAINED_GLASS_PANE, "§f"));
		}
		setItem(4, new GuiItemStack(Material.LIME_STAINED_GLASS_PANE, "§f"));
		setItem(22, new GuiItemStack(Material.LIME_STAINED_GLASS_PANE, "§f"));
		viewer.updateInventory();
	}

	private void drawPreview() {
		lock();
		List<SingleItem> preview = _case.getPreviewableItems();

//        viewer.sendMessage(preview.size() + " items preview");

		new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {
				if (viewer == null || !viewer.isOnline() || !isViewing(viewer)) {
					cancel();
					return;
				}
				if (i < 27 && i < preview.size()) {
					SingleItem item = preview.get(i);
					setItem(i, new GuiItemStack(item.getItem().getGuiItem()));
					viewer.updateInventory();
					viewer.playSound(viewer.getEyeLocation(), "ui.button.click", 0.5f, 1f);
				} else {
					List<CaseKeyPI> keys;
					CasePI current;
					if ((current = _case.getAvailableCase(viewer)) != null
							&& (keys = _case.getAvailableKeys(viewer)) != null && keys.size() > 0) {
						ItemStack i = _case.getKeyIcon();
						ItemMeta im = i.getItemMeta();
						im.setDisplayName("§2✔ §aCòn §e" + keys.size() + " §achìa khoá");
						List<String> lores = new ArrayList<>();
						lores.add("§e<Click để mở>");
						im.setLore(lores);
						i.setItemMeta(im);
						setItem(31, new GuiItemStack(i) {
							@Override
							public void onClick(InventoryClickEvent e) {
								lock();
								Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
									var data = PlayerData.getData(viewer);
									CaseKeyPI key = data.getItem(CaseKeyPI.class, _case.getID());
									CasePI casepi = data.getItem(CasePI.class, _case.getID());
									if (key != null && casepi != null) {
										try {
											data.removeItem(key, "use key");
											data.removeItem(casepi, "use case");
											Bukkit.getScheduler().runTask(main, () -> {
												unlock();
												open();
											});
										} catch (Exception e1) {
											main.getLogger().log(Level.SEVERE, "Co loi xay ra khi check key", e1);
											viewer.sendMessage("§cCó lỗi xảy ra, vui lòng thử lại sau.");
										}
									}
								});
							}
						});
					} else {
						if (current == null) {
							setItem(31, new GuiItemStack(Material.BARRIER, "§4✖ §cĐã hết rương",
									"§eHãy thu thập hoặc", "§etìm mua ở shop"));
						} else {
							setItem(31, new GuiItemStack(Material.BARRIER, "§4✖ §cĐã hết chìa khoá",
									"§e<Click để mua>") {
								@Override
								public void onClick(InventoryClickEvent e) {
									lock();
									_case.openKeyShop(viewer);
								}
							});
						}
					}
					viewer.updateInventory();
					cancel();
					unlock();
					return;
				}
				i++;
			}
		}.runTaskTimer(main, 0, 2);
	}

	private void clearPreview() {
		for (int i = 0; i < 27; i++) {
			setItem(i, null);
		}
	}

	public void open() {
		if (viewer == null || !viewer.isOnline() || !isViewing(viewer)) {
			return;
		}
//        viewer.sendMessage("open request triggered");
		lock();
		drawOpenBorder();

		new BukkitRunnable() {

			int tick = 0, every = 1;
			SingleItem[] items = new SingleItem[7];

			@Override
			public void run() {
				try {
					if (viewer == null || !viewer.isOnline()) {
						cancel();
						return;
					}
					if (!isViewing(viewer)) {
						viewer.openInventory(getInventory());
					}
					if (tick % every == 0) {
						for (int i = 0; i < 7; i++) {
							if (i < 6) {
								items[i] = items[i + 1];
							} else {
//                            SingleItem item = _case.getRandomItem();
								items[i] = _case.getRandomItem(); //; new GuiItemStack(item.getGuiItem());
							}
							if (items[i] != null) {
								PlayerAward award = items[i].getItem();
								ItemStack item = award.getGuiItem();
								if (award.getUnit() != null) {
									ItemMeta im = item.getItemMeta();
									List<String> lore = new ArrayList<>();
									lore.add(" §b(" + award.getUnit().toLowerCase() + ")");
									if (im != null && im.getLore() != null) {
										lore.addAll(im.getLore());
									}
									im.setLore(lore);
									item.setItemMeta(im);
								}
								setItem(10 + i, new GuiItemStack(item));
							}
						}
						viewer.playSound(viewer.getEyeLocation(), "ui.button.click", 0.5f, 1f);
					}
					if (tick > 60)
						every = 2;
					if (tick > 90)
						every = 3;
					if (tick > 105)
						every = 4;
					if (tick > 120)
						every = 5;
					if (tick > 150)
						every = 10;
					if (tick > 170)
						every = 20;
					if (tick > 190)
						every = 40;
					if (tick > 220) {
						cancel();
						Utils.runNotSync(() -> {
							SingleItem reward = items[3];
							if (reward != null) {
								PlayerAward item = reward.getItem();
								try {
									item.apply(viewer, "got from case " + _case.getID());
									String name = item.getName() + (item.getUnit() == null
											? "" : (" §b(" + item.getUnit().toLowerCase() + ")"));
									viewer.sendMessage("§aBạn vừa nhận được: " + name);
									if (reward.toBeAnnounced()) {
										Utils.runSync(() -> viewer.getWorld().playSound(
											viewer.getEyeLocation(),
											"ui.toast.challenge_complete",
											100, 1
										));
										Utils.bungeeMessaging(
											viewer,
											"msbc",
											"§f;§eChúc mừng §d§l" + viewer.getName() + " §evừa quay trúng "
													+ name + " §etừ rương " + _case.getName() + ";§f",
											""
										);
									}
								} catch (Exception e) {
									main.getLogger().log(Level.SEVERE, "Không thể apply MSItem ("
											+ item.getGuiItem().getItemMeta().getDisplayName() + ") cho "
											+ viewer.getName() + " khi mở case " + _case.getID(), e);
									viewer.sendMessage("§cCó lỗi xảy ra khi trao thưởng quay rương, vui lòng báo cho admin"
											+ " biết kèm thời gian chính xác để được xử lý!");
								}
							}
							Bukkit.getScheduler().runTaskLater(main, () -> {
								if (isViewing(viewer)) {
									var data = PlayerData.getData(viewer);
									CaseKeyPI key = data.getItem(CaseKeyPI.class, _case.getID());
									CasePI casepi = data.getItem(CasePI.class, _case.getID());
									if (key != null || casepi != null) {
										clearPreview();
										drawPreview();
									} else {
										viewer.closeInventory();
									}
								}
							}, 60);
						});
					}
					tick++;
				} catch (Exception e) {
					main.getLogger().log(Level.SEVERE, viewer.getName() + " bị lỗi khi quay case "
							+ _case.getID(), e);
					viewer.sendMessage("§cCó lỗi xảy ra trong quá trình quay case. Vui lòng báo cho admin kèm"
							+ " thời gian chính xác để được xử lý và đền bù!");
				}
			}
		}.runTaskTimer(main, 0, 1);
	}
}
