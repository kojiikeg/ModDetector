package madpillow.simpleModDetector;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SimpleModDetector extends JavaPlugin implements Listener, PluginMessageListener {

	@Override
	public void onEnable() {
		if (Bukkit.getServer().getClass().getPackage().getName().contains("1.13")) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getPluginManager().registerEvents(this, this);

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FML|HS");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "FML|HS", this);

		Commands Commands = new Commands();
		getCommand("modlist").setExecutor(Commands);
	}

	@EventHandler
	public void onChannel(PlayerRegisterChannelEvent e) {
		if (e.getChannel().equals("FORGE")) {
			e.getPlayer().sendPluginMessage(this, "FML|HS", new byte[] { -2, 0 });
			e.getPlayer().sendPluginMessage(this, "FML|HS", new byte[] { 0, 2, 0, 0, 0, 0 });
			e.getPlayer().sendPluginMessage(this, "FML|HS", new byte[] { 2, 0, 0, 0, 0 });
		}
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] data) {
		final Pattern ptnUnknown = Pattern.compile(
				"[\u0000-\u0009\u000B\u000C\u000E-\u001F]");
		final Pattern verPtn = Pattern.compile(
				" ([0-9]|\\.)+");
		if (data[0] == 2) {
			byte[] temp = new byte[data.length - 2];
			for (int i = 2; i < data.length; i++) {
				temp[i - 2] = data[i];
			}
			data = temp;

			String mods = null;

			try {
				mods = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Matcher m = ptnUnknown.matcher(mods);
			while (m.find()) {
				mods = mods.replace(m.group(), " ");
			}
			Matcher mm = verPtn.matcher(mods);
			String result = mods;
			while (mm.find()) {
				result = result.replace(mm.group(), "\t" + mm.group() + "\t");
			}
			mods = result;

			String[] sp = mods.split("\t");

			Map<String, String> map = new HashMap<>();
			for (int i = 0; i < sp.length - 1; i++) {
				map.put(sp[i], sp[++i]);
			}
			player.setMetadata("modlist", new FixedMetadataValue(this, map));
		}

	}
}
