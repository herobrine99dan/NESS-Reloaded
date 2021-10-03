package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AimbotPrediction extends PacketCheck {

    public static final CheckInfo checkInfo = CheckInfos.forPackets();
    private final List<String> resultStrings;
    private int dataCollected = 0;

    public AimbotPrediction(PacketCheckFactory<?> factory, NessPlayer player) {
        super(factory, player);
        resultStrings = new ArrayList<>();
        resultStrings.add("yaw;pitch;gcd;sensPercent;mouseDeltaX,mouseDeltaY");
    }

    private float lastYaw, lastPitch, lastYawDiff, lastPitchDiff, lastXDelta, lastYDelta;

    @Override
    protected void checkPacket(Packet packet) {
        if (!packet.isPacketType(this.packetTypeRegistry().playInFlying()) || player().isTeleported()) {
            return;
        }
        PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
        if (!wrapper.hasLook()) {
            return;
        }
        float yaw = wrapper.yaw();
        float pitch = wrapper.pitch();
        float yawDiff = yaw - lastYaw;
        float pitchDiff = pitch - lastPitch;
        try {
            calculateAndStoreMouseDeltas(packet, yawDiff, pitchDiff);
        } catch (IOException ex) {
            Logger.getLogger(AimbotPrediction.class.getName()).log(Level.SEVERE, null, ex);
        }
        lastYaw = wrapper.yaw();
        lastPitch = wrapper.pitch();
        lastYawDiff = yawDiff;
        lastPitchDiff = pitchDiff;
    }

    private void calculateAndStoreMouseDeltas(Packet packet, float yawDiff, float pitchDiff) throws IOException {
        float xDelta = round((yawDiff - lastYawDiff) / this.player().getGcd());
        float yDelta = round((pitchDiff + lastPitchDiff) / this.player().getGcd());
        // The checks
        costantRotation(xDelta, yDelta);
        if (this.player().isDebugMode()) {
            this.player().sendDevMessage("xDelta: " + xDelta + " yDelta: " + yDelta + " dataCollected: " + dataCollected);
            if (dataCollected < 250) {
                updateExcelData(yawDiff, pitchDiff, this.player().getGcd(), xDelta, yDelta);
            } else {
                save();
                this.player().sendDevMessage("Saving");
                dataCollected = 0;
            }
        }
        lastXDelta = xDelta;
        lastYDelta = yDelta;
    }

    private String fixValue(float f) {
        return Float.toString(f).replace(".", ",");
    }

    private void save() throws IOException {
        File newFile = new File(this.ness().getPlugin().getDataFolder(), "MouseData" + System.nanoTime() / 100000 + ".cls");
        newFile.createNewFile();
        for (String s : resultStrings) {
            String resulter = s + System.lineSeparator();
            Files.write(newFile.toPath(), resulter.getBytes(), StandardOpenOption.APPEND);
        }
    }

    private float costantRotation = 0.0f;

    private void updateExcelData(float yawDiff, float pitchDiff, float gcd, float xDelta, float yDelta) {
        try {
            String st = fixValue(yawDiff) + ";" + fixValue(pitchDiff) + ";" + fixValue(gcd) + ";" + fixValue((float) this.player().getSensitivity()) + ";" + fixValue(xDelta) + ";" + fixValue(yDelta);
            resultStrings.add(st);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dataCollected++;
    }

    private void costantRotation(float xDelta, float yDelta) {
        if (xDelta == 0.0 || yDelta == 0.0) { //Hey, This can't happen normally! Detects costant aiming. (when yawDiff=0.0 then 0.0/gcd=0.0)
            if (++costantRotation > 1) { //Sometimes this happens in Vanilla Minecraft, but it can't happen for a lot of rotations!
                this.flag(" CostantRotation");
            }
        } else if (costantRotation > 0.0) {
            costantRotation -= 0.5;
        }
    }

    private float round(float n) {
        float places = 100.0f;
        return Math.round(n * places) / places;
    }
}
