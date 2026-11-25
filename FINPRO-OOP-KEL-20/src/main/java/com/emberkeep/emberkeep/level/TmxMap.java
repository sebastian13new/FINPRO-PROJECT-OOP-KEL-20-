package com.emberkeep.emberkeep.level;

import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Membaca file map buatan aplikasi Tiled (.tmx). */
public class TmxMap {
    /** Satu objek dari layer Objects, misalnya Player, ChestSword, atau Boss. */
    public record MapObject(String type, double x, double y) { }

    private final int width;
    private final int height;
    private final int tileSize;
    private final int[][] gids;
    private final Map<Integer, TileType> tileTypes = new HashMap<>();
    private final List<MapObject> objects = new ArrayList<>();
    private int firstGid = 1;
    private int columns = 1;
    private String imageUrl;
    private Image tilesetImage;

    private TmxMap(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.gids = new int[height][width];
    }

    /** Contoh: TmxMap.load("/maps/gudang_tua.tmx") */
    public static TmxMap load(String resourcePath) {
        try (InputStream in = TmxMap.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IllegalStateException("File map tidak ditemukan: " + resourcePath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            Element root = doc.getDocumentElement();
            TmxMap map = new TmxMap(intAttr(root, "width"), intAttr(root, "height"), intAttr(root, "tilewidth"));
            map.readTileset(root, resourcePath);
            map.readTiles(root);
            map.readObjects(root);
            return map;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Gagal membaca map " + resourcePath + ": " + e.getMessage(), e);
        }
    }

    private void readTileset(Element root, String resourcePath) {
        Element tileset = firstChild(root, "tileset");
        if (tileset == null) return;
        if (tileset.hasAttribute("source")) {
            throw new IllegalStateException("Tileset di " + resourcePath + " belum di-embed. Di Tiled: Map > Embed Tilesets.");
        }
        firstGid = intAttr(tileset, "firstgid");
        columns = Math.max(1, intAttr(tileset, "columns"));

        Element image = firstChild(tileset, "image");
        if (image != null) {
            String folder = resourcePath.substring(0, resourcePath.lastIndexOf('/') + 1);
            URL url = TmxMap.class.getResource(folder + image.getAttribute("source"));
            if (url != null) imageUrl = url.toExternalForm();
        }

        NodeList tiles = tileset.getElementsByTagName("tile");
        for (int i = 0; i < tiles.getLength(); i++) {
            Element tile = (Element) tiles.item(i);
            NodeList properties = tile.getElementsByTagName("property");
            for (int j = 0; j < properties.getLength(); j++) {
                Element property = (Element) properties.item(j);
                if ("type".equalsIgnoreCase(property.getAttribute("name"))) {
                    TileType type = TileType.valueOf(property.getAttribute("value").trim().toUpperCase());
                    tileTypes.put(firstGid + intAttr(tile, "id"), type);
                }
            }
        }
    }

    private void readTiles(Element root) {
        NodeList layers = root.getElementsByTagName("layer");
        if (layers.getLength() == 0) return;
        Element data = firstChild((Element) layers.item(0), "data");
        if (data == null || !"csv".equals(data.getAttribute("encoding"))) {
            throw new IllegalStateException("Layer harus berformat CSV. Di Tiled: Map > Map Properties > Tile Layer Format = CSV.");
        }
        String[] values = data.getTextContent().trim().split("[,\\s]+");
        for (int i = 0; i < values.length && i < width * height; i++) {
            long raw = Long.parseLong(values[i].trim());
            gids[i / width][i % width] = (int) (raw & 0x1FFFFFFFL);   // buang tanda flip dari Tiled
        }
    }

    private void readObjects(Element root) {
        NodeList list = root.getElementsByTagName("object");
        for (int i = 0; i < list.getLength(); i++) {
            Element object = (Element) list.item(i);
            String type = object.hasAttribute("class") ? object.getAttribute("class") : object.getAttribute("type");
            objects.add(new MapObject(type.trim(), doubleAttr(object, "x"), doubleAttr(object, "y")));
        }
    }

    private static Element firstChild(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? (Element) list.item(0) : null;
    }

    private static int intAttr(Element element, String name) {
        String value = element.getAttribute(name);
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private static double doubleAttr(Element element, String name) {
        String value = element.getAttribute(name);
        return value.isEmpty() ? 0 : Double.parseDouble(value);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTileSize() { return tileSize; }
    public List<MapObject> getObjects() { return objects; }

    public int getGid(int col, int row) {
        if (col < 0 || row < 0 || col >= width || row >= height) return 0;
        return gids[row][col];
    }

    /** Jenis petak untuk logika game. Petak kosong dianggap dinding. */
    public TileType getTileType(int col, int row) {
        int gid = getGid(col, row);
        if (gid == 0) return TileType.WALL;
        return tileTypes.getOrDefault(gid, TileType.FLOOR);
    }

    /** Gambar tileset baru dimuat saat pertama kali dibutuhkan (aman untuk unit test). */
    public Image getTilesetImage() {
        if (tilesetImage == null && imageUrl != null) tilesetImage = new Image(imageUrl);
        return tilesetImage;
    }

    public double getSourceX(int gid) { return ((gid - firstGid) % columns) * tileSize; }
    public double getSourceY(int gid) { return ((gid - firstGid) / columns) * tileSize; }
}
