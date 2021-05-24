package be.kuleuven.tempotyping.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerInfo implements Parcelable {
    private boolean regularGame;
    private long id;
    private long wpm;
    private int accuracy;

    public PlayerInfo(boolean regularGame) {
        this.regularGame = regularGame;
    }

    public PlayerInfo(boolean regularGame, long id, long wpm, int accuracy) {
        this.regularGame = regularGame;
        this.id = id;
        this.wpm = wpm;
        this.accuracy = accuracy;
    }

    protected PlayerInfo(Parcel in) {
        this.regularGame = in.readByte() == 1;

        if (in.dataAvail() > 0) {
            id = in.readLong();
            wpm = in.readLong();
            accuracy = in.readInt();
        }
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (regularGame ? 1 : 0));
        dest.writeLong(id);
        dest.writeLong(wpm);
        dest.writeInt(accuracy);
    }

    public String gamemode() {
        if (regularGame) {
            return "Regular";
        } else {
            return "Scramble";
        }
    }

    public String placementURL() {
        return gamemode() + "/" + id;
    }

    public boolean isRegularGame() {
        return regularGame;
    }

    public long getWpm() {
        return wpm;
    }

    public int getAccuracy() {
        return accuracy;
    }
}
