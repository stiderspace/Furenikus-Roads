package com.silvaniastudios.roads.registries;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silvaniastudios.roads.FurenikusRoads;
import com.silvaniastudios.roads.blocks.FRBlocks;
import com.silvaniastudios.roads.blocks.PaintColour;
import com.silvaniastudios.roads.blocks.paint.CustomPaintBlock;

public class DynamicBlockRegistry {
	
	public static ArrayList<CustomPaintBlock> customPaints = new ArrayList<CustomPaintBlock>();
	
	public static void register() {
		FurenikusRoads.debug(0, "Starting to load custom paint files");
		ArrayList<File> jsonList = new ArrayList<File>();
		jsonList = getJsonFiles("./mods/RoadPaints/", jsonList);
		
		if (jsonList.size() > 0) {
			FurenikusRoads.debug(0, "Found " + jsonList.size() + " custom road files. Loading...");
			int success = 0;
			for (int a = 0; a < FRBlocks.col.length; a++) {
				for (int i = 0; i < jsonList.size(); i++) {
					CustomPaintBlock block = importedBlock(jsonList.get(i), FRBlocks.col[a]);
					if (block != null) {
						customPaints.add(block);
						success++;
					} else {
						FurenikusRoads.debug(0, jsonList.get(i).getName() + " failed to load. Skipping...");
					}
				}
			}
			
			FurenikusRoads.debug(0, "Custom paint files loading complete, " + success + "/" + jsonList.size()*FRBlocks.col.length + " (with " + FRBlocks.col.length + " colour variants) files loaded successfully.");
		} else {
			FurenikusRoads.debug(0, "No custom paint files found; skipping custom road loading.");
		}
		
		
	}
	
	private static ArrayList<File> getJsonFiles(String dir, ArrayList<File> jsonList) {
		File directory = new File(dir);
		
		if (!directory.exists()) {
			FurenikusRoads.debug(0, "Custom paint directory is missing; creating...");
			directory.mkdir();
		}
		
		File[] files = directory.listFiles();
		
		for (File file : files) {
			if (file.isFile()) {
				jsonList.add(file);
			} else if (file.isDirectory()) {
				getJsonFiles(file.getAbsolutePath(), jsonList);
			}
		}
		
		return jsonList;
	}
	
	private static CustomPaintBlock importedBlock(File file, PaintColour col) {
		JsonParser parser = new JsonParser();
		CustomPaintBlock block = null;
		
		try {
			JsonObject json = (JsonObject) parser.parse(new FileReader(file));
			
			String name = json.has("blockName") ? json.get("blockName").getAsString() : file.getName();
			String localName = json.has("localName") ? json.get("localName").getAsString() : "NAME_READ_FAILED";
			String type = json.has("category") ? json.get("category").getAsString() : "line";
			JsonArray grid = json.has("grid") ? json.get("grid").getAsJsonArray() : null;
			//type
			
			for (int i = 0; i < FRBlocks.col.length; i++) {
				if (name.toLowerCase().contains(FRBlocks.col[i].getName())) {
					FurenikusRoads.debug(0, "Paint JSON file " + name + " contains illegal word. Please don't use \"" + FRBlocks.col[i].getName() + "\" anywhere in your paint name.");
				}
			}
			
			if (grid != null) {
				if (!(grid.size() == 16 || grid.size() == 32)) {
					FurenikusRoads.debug(0, "Paint JSON file " + name + " has malformed Y axis grid. Make sure there are exactly 16 or 32 entries.");
					return null;
				}
				boolean[][] gridArray = new boolean[grid.size()][grid.size()];
				
				for (int i = 0; i < grid.size(); i++) {
					JsonArray gridRow = grid.get(i).getAsJsonArray();
					if (gridRow.size() != grid.size()) {
						FurenikusRoads.debug(0, "Paint JSON file " + name + " has malformed X axis grid. Make sure there are exactly " + grid.size() + " entries.");
						return null;
					}
					for (int j = 0; j < gridRow.size(); j++) {
						char c = gridRow.get(j).getAsCharacter();
						if (c == ' ' || c == '0') {
							gridArray[j][i] = false;
						} else {
							gridArray[j][i] = true;
						}
					}
				}
				
				block = new CustomPaintBlock(name + "_" + col.getName(), localName, gridArray, type, col);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return block;
	}
}
