package com.gamingmesh.jobs.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class ExploreChunk {

    private int x;
    private int z;
    private Set<Integer> playerIds = new HashSet<>();
    private boolean full = false;
    private Integer dbId = null;
    private boolean updated = false;

    public ExploreChunk(int playerId, int x, int z) {
	this.x = x;
	this.z = z;
	this.playerIds.add(playerId);
    }

    public ExploreChunk(int x, int z) {
	this.x = x;
	this.z = z;
    }

    public ExploreRespond addPlayer(int playerId) {
	if (full) {
	    return new ExploreRespond(Jobs.getExplore().getPlayerAmount() + 1, false);
	}
	boolean newChunkForPlayer = false;
	if (!playerIds.contains(playerId)) {
	    if (playerIds.size() < Jobs.getExplore().getPlayerAmount()) {
		playerIds.add(playerId);
		updated = true;
	    }
	    newChunkForPlayer = true;
	}

	if (playerIds.size() >= Jobs.getExplore().getPlayerAmount()) {
	    this.full = true;
	    if (Jobs.getGCManager().ExploreCompact)
		playerIds = null;
	}

	return new ExploreRespond(newChunkForPlayer ? getPlayers().size() : getPlayers().size() + 1, newChunkForPlayer);
    }

    public boolean isAlreadyVisited(int playerId) {
	if (full)
	    return true;
	return playerIds.contains(playerId);
    }

    public int getCount() {
	if (full)
	    return Jobs.getExplore().getPlayerAmount();
	return playerIds.size();
    }

    public int getX() {
	return x;
    }

    public int getZ() {
	return z;
    }

    public Set<Integer> getPlayers() {
	return playerIds == null ? new HashSet<>() : playerIds;
    }

    public String serializeNames() {
	String s = "";
	if (playerIds == null)
	    return null;
	for (Integer one : this.playerIds) {
	    if (!s.isEmpty())
		s += ";";
	    s += one;
	}
	return s;
    }

    public void deserializeNames(String names) {
	if (names == null || names.isEmpty()) {
	    this.full = true;
	    playerIds = null;
	    return;
	}

	List<String> split = Arrays.asList(names.split(";"));
	for (String one : split) {
	    try {
		int id = Integer.parseInt(one);
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(id);
		if (info != null)
		    playerIds.add(id);
	    } catch (Exception | Error e) {
		updated = true;
		JobsPlayer jp = Jobs.getPlayerManager().getJobsPlayer(one);
		if (jp != null)
		    playerIds.add(jp.getUserId());
	    }
	}

	if (playerIds.size() >= Jobs.getExplore().getPlayerAmount()) {
	    this.full = true;
	    if (Jobs.getGCManager().ExploreCompact) {
		playerIds = null;
		if (!names.isEmpty())
		    updated = true;
	    }
	}
    }

    public Integer getDbId() {
	return dbId;
    }

    public void setDbId(Integer dbId) {
	this.dbId = dbId;
    }

    public boolean isUpdated() {
	return updated;
    }

    public void setUpdated(boolean updated) {
	this.updated = updated;
    }

    public boolean isFullyExplored() {
	return full;
    }
}
