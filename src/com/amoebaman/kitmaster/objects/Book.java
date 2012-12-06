package com.amoebaman.kitmaster.objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import com.amoebaman.kitmaster.KitMaster;

/**
 * 
 * User-friendly interface for manipulating book pages/titles/authors.
 * Uses MC-native NBT tags to manage data.
 * 
 * @author Someone else
 *
 */
public class Book{

	private net.minecraft.server.ItemStack item = null;
	private CraftItemStack stack = null;
	private static String LINE_BREAK_SEQUENCE = "|n|";

	/**
	 * Constructs the wrapper for an <code>ItemStack</code>.
	 * @param item The item to wrap.
	 */
	public Book(org.bukkit.inventory.ItemStack item) {
		if(item instanceof CraftItemStack) {
			stack = (CraftItemStack)item;
			this.item = stack.getHandle();
		}
		else if(item instanceof org.bukkit.inventory.ItemStack) {
			stack = new CraftItemStack(item);
			this.item = stack.getHandle();
		}
	}
	
	/**
	 * Constructs a book by reading its contents from a file.
	 * @param file The file to read from.
	 */
	public Book(File file){
		stack = new CraftItemStack(Material.WRITTEN_BOOK, 1);
		this.item = stack.getHandle();
		try{
			BufferedReader scribe = new BufferedReader(new FileReader(file));
			setTitle(scribe.readLine());
			setAuthor(scribe.readLine());
			while(scribe.ready())
				addPages(new String[]{ scribe.readLine().replace(LINE_BREAK_SEQUENCE, "\n") });
			scribe.close();
		}
		catch(Exception e){
			KitMaster.logger().severe("Failed to read book from file - " + e.getMessage());
		}
	}

	/**
	 * Gets the pages contained by this book.
	 * @return The pages.
	 */
	public String[] getPages() {
		NBTTagCompound tags = item.getTag();
		if(tags == null)
			return null;
		NBTTagList pages = tags.getList("pages");
		String[] pageTexts = new String[pages.size()];
		for(int i = 0; i < pages.size(); i++)
			pageTexts[i] = pages.get(i).toString();
		return pageTexts;
	}

	/**
	 * Gets the author of this book.
	 * @return The author.
	 */
	public String getAuthor() {
		NBTTagCompound tags = item.getTag();
		if(tags == null)
			return null;
		String author = tags.getString("author");
		return author;
	}

	/**
	 * Gets the title of this book.
	 * @return The title.
	 */
	public String getTitle() {
		NBTTagCompound tags = item.getTag();
		if(tags == null)
			return null;
		String title = tags.getString("title");
		return title;
	}

	/**
	 * Sets the book's pages.
	 * @param newPages The new pages.
	 */
	public void setPages(String[] newPages) {
		NBTTagCompound tags = item.tag;
		if (tags == null)
			tags = item.tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList("pages");
		if(newPages == null || newPages.length == 0) 
			pages.add(new NBTTagString("1", ""));
		else
			for(int i = 0; i < newPages.length; i++)
				pages.add(new NBTTagString("" + i + "", newPages[i]));
		tags.set("pages", pages);
	}

	/**
	 * Adds pages to the book.
	 * @param newPages The pages to add.
	 */
	public void addPages(String[] newPages) {
		NBTTagCompound tags = item.tag;
		if (tags == null)
			tags = item.tag = new NBTTagCompound();
		NBTTagList pages;
		if(getPages() == null)
			pages = new NBTTagList("pages");
		else
			pages = tags.getList("pages");
		if((newPages == null || newPages.length == 0) && pages.size() == 0)
			pages.add(new NBTTagString("1", ""));
		else
			for(int i = 0; i < newPages.length; i++)
				pages.add(new NBTTagString("" + pages.size() + "", newPages[i]));
		tags.set("pages", pages);
	}

	/**
	 * Sets the author of this book.
	 * @param author The new author.
	 */
	public void setAuthor(String author) {
		NBTTagCompound tags = item.tag;
		if (tags == null)
			tags = item.tag = new NBTTagCompound();
		if(author != null && !author.equals(""))
			tags.setString("author", author);
	}

	/**
	 * Sets the title of this book.
	 * @param title The new title.
	 */
	public void setTitle(String title) {
		NBTTagCompound tags = item.tag;
		if (tags == null)
			tags = item.tag = new NBTTagCompound();
		if(title != null && !title.equals(""))
			tags.setString("title", title);
	}

	/**
	 * Saves this book to a file.
	 * @param file The file to save the book to.
	 */
	public void saveToFile(File file){
		try{
			BufferedWriter scribe = new BufferedWriter(new FileWriter(file));
			scribe.write(getTitle());	
			scribe.newLine();
			scribe.write(getAuthor());
			scribe.newLine();
			for(String line : getPages()){
				scribe.write(line.replace("\n", LINE_BREAK_SEQUENCE));
				scribe.newLine();
			}
			scribe.close();
		}
		catch(Exception e){
			KitMaster.logger().severe("Failed to write book to file - " + e.getMessage());
		}
	}

	/**
	 * Gets the ItemStack that this Book is currently wrapping.
	 * @return The <code>ItemStack</code> that this Book represents.
	 */
	public org.bukkit.inventory.ItemStack getItemStack() {
		return stack;
	}
	
	/**
	 * Sets the character that will replace and be replaced by in-page line breaks when saving to and loading from files.  This character should be one that would never appear in a book's text.
	 * @param sequence The new line break character.
	 */
	public static void setLineBreakSequence(String sequence){
		Book.LINE_BREAK_SEQUENCE = sequence;
	}

}