package org.skydream.quizcraft;

import net.minecraft.resources.ResourceLocation;

public class Reward {
    private String itemId;
    private int maxAmount;

    public Reward() {}

    public Reward(String itemId, int maxAmount) {
        this.itemId = itemId;
        this.maxAmount = maxAmount;
    }

    public String getItemId() { return itemId; }
    public int getMaxAmount() { return maxAmount; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setMaxAmount(int maxAmount) { this.maxAmount = maxAmount; }

    // 使用 ResourceLocation 的静态方法
    public ResourceLocation getItemResourceLocation() {
        return ResourceLocation.parse(itemId);
    }
}