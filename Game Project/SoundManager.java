public class SoundManager {
    public Sound launcherSound;
    public Sound fireworkLoopSound;
    public Sound boostSound;
    public Sound damageSound;
    public Sound powerSound;
    public Sound fuelBlastSound;
    public Sound bgmSound;
    public Sound moneySound;
    public Sound failSound;
    public Sound endSound;

    private float effectVolume = 0.8f;
    private float bgmVolume = 0.8f;

    public SoundManager() {
        launcherSound = new Sound("Sound/Luncher.wav", false);
        fireworkLoopSound = new Sound("Sound/FireworkSound.wav", false);
        boostSound = new Sound("Sound/Boost.wav", false);
        damageSound = new Sound("Sound/Damage.wav", false);
        powerSound = new Sound("Sound/Power.wav", false);
        fuelBlastSound = new Sound("Sound/FireworkBlast.wav", false);
        bgmSound = new Sound("Sound/MenuBGM.wav", true);
        moneySound = new Sound("Sound/Money.wav", false);
        failSound = new Sound("Sound/Fail.wav", false);
        endSound = new Sound("Sound/End.wav", false);

        setEffectVolume(effectVolume);
        setBgmVolume(bgmVolume);
    }

    public void setEffectVolume(float volume) {
        this.effectVolume = volume;
        if (moneySound != null) moneySound.setVolume(volume);
        if (launcherSound != null) launcherSound.setVolume(volume);
        if (fireworkLoopSound != null) fireworkLoopSound.setVolume(volume);
        if (boostSound != null) boostSound.setVolume(volume);
        if (damageSound != null) damageSound.setVolume(volume);
        if (powerSound != null) powerSound.setVolume(volume);
        if (fuelBlastSound != null) fuelBlastSound.setVolume(volume);
        if (failSound != null) failSound.setVolume(volume);
        if (endSound != null) endSound.setVolume(volume);
    }

    public void setBgmVolume(float volume) {
        this.bgmVolume = volume;
        if (bgmSound != null) bgmSound.setVolume(volume);
    }

    public float getEffectVolume() {
        return effectVolume;
    }

    public float getBgmVolume() {
        return bgmVolume;
    }

    public void stopAllSounds() {
        if (bgmSound != null) bgmSound.stop();
        if (launcherSound != null) launcherSound.stop();
        if (fireworkLoopSound != null) fireworkLoopSound.stop();
        if (boostSound != null) boostSound.stop();
        if (damageSound != null) damageSound.stop();
        if (powerSound != null) powerSound.stop();
        if (fuelBlastSound != null) fuelBlastSound.stop();
        if (failSound != null) failSound.stop();
        if (endSound != null) endSound.stop();
    }
}
