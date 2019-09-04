package me.lordraindance2.sweetdreams.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.lordraindance2.sweetdreams.violation.ViolationManager;
import me.lordraindance2.sweetdreams.violation.ViolationType;
import org.bukkit.entity.Player;

public class ClientSpeedUp extends Check {
    private long time;
    private double balancer;
    private long join;

    public ClientSpeedUp(Player player) {
        super(CheckType.SPEEDUP, player,
                PacketType.Play.Client.FLYING,
                PacketType.Play.Client.POSITION);
        this.balancer = 0D;
        this.join = System.currentTimeMillis();
    }

    @Override
    public void recieve(PacketContainer packet) {
        if(packet.getType() == PacketType.Play.Client.FLYING) {
            this.time = this.time == 0 ? System.currentTimeMillis() - 50 : this.time;

            long delta = System.currentTimeMillis() - time;
            this.time = System.currentTimeMillis();
            if(delta >= 100) delta = 50;
            balancer += 50;
            balancer -= delta;
            //log(balancer + " " + delta);
            if (balancer > 15) {
                //if the player has recently joined at least 7 seconds ago,
                //don't flag
                if(System.currentTimeMillis() - join <= 7000L) return;
                flag();
                balancer = 0;
            }
            if(balancer < -100) balancer = 0;
        }else if(packet.getType() == PacketType.Play.Client.POSITION) {
            balancer -= 50;
        }
    }

    @Override
    public void send(PacketContainer event) {

    }

    @Override
    public void flag() {
        ViolationManager.addViolation(getPlayer(), ViolationType.SERIOUS, getCheckType());
    }
}
