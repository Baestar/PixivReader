
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.MicrosoftTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.HttpRequest;
import com.jaunt.JauntException;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;
import com.jaunt.component.Form;

public class PixivReader
{
	//fields
    String filePath;
    UserAgent uA;
    String userName;
    String password;
    
    //constructors
    PixivReader(String user, String pass)
    {
    	filePath = "";
    	uA = new UserAgent();
    	userName = user;
    	password = pass;
    	login();
    }
    PixivReader(String user, String pass, String filePath)
    {
    	this.filePath = filePath;
    	uA = new UserAgent();
    	userName = user;
    	password = pass;
    	login();
    }
    
    //functions
    /**
     * Takes a string beginning with one of the following characters "s" for Single, 
     * "m" for Multi, "p" for Page, "a" for Artist or "u" for User, or "f" for Filepath.
     * The function calls the relevant function with an argument formed by the rest of the 
     * string beyond the first letter. In the case of "f", it sets the filepath to the remainder
     * of the string. This string should be the absolute path to the folder where the output will
     * be saved.
     * @param arg A signaling letter, followed by data to be passed on
     * @throws JauntException
     */
    public void parseArgs(String arg)throws JauntException
    {
        String function = arg.substring(0,1);
        String data = arg.substring(1,arg.length());
        switch(function)
        {
            case "s":   getSingle(data);
                        break;
                        
            case "m":   getMulti(data);
            			break;
            
            case "p":   getPage(data);
                        break;
            
            case "a" :            
            case "u" :  getArtist(data);
                        break;
                        
            case "f":   filePath = data + "/";
                        break;
                      
            
            
            
        }
    }
    /**
     * Creates one or two image files at the location of filePath. If the source image is a png
     * this will save the png, and also create a jpg copy which it will also save. If the source
     * is a jpg, it will only create the jpg file. In both cases the jpg file is returned.
     * @param src The direct image link to a Pixiv image.
     * @param title The desired title for the image(s).
     * @return The written jpg file.
     */
    public File getImage(String src, String title)
	{
	    try
	    {
	        URL url = new URL(src);
	        
	        URLConnection connection = url.openConnection();
	        connection.setRequestProperty("Referer", "http://www.pixiv.net/");
	               
	        InputStream stream = connection.getInputStream();
	        BufferedImage image = null;
	        String filetype = src.substring(src.length()-3);
	                    
	        image = ImageIO.read(stream);
	        BufferedImage newBI = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);
	        newBI.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
	        File f = createFile(title, "jpg");
	        ImageIO.write(newBI, "jpg", f);
	        if(filetype.equals("png"))
	        {
	            String name = f.getName();
	            name = name.substring(0, name.length()-4);
	            ImageIO.write(image, "png", createFile(name, "png"));
	        }
	        return f;
	    }
	    catch(IOException e)
	    {
	           e.printStackTrace();
	    }
	    return null;
	}
    /**
     * Gets an image from a Pixiv art page and saves it. The location it is saved in is a folder
     * in the current filePath. This folder will be named for the artist on the page, if 
     * such a folder doesn't exist it will be created. This function will check a xml file to see if
     * the art has already been saved; If it has, the function will end without saving another copy. If the
     * artwork is saved, it will mark the xml file as such. If such an xml file doesn't exist it
     * will be created in the same folder as the art.
     * @param unique A unique identifier for the art page, this should be either the page url or
     * the illustration id. 
     * @throws JauntException
     */
	public void getSingle(String unique)throws JauntException
    {
            Element el;
            String id = urlToID(unique);
            String backURL = uA.doc.getUrl();
            HttpRequest http = HttpRequest.makeGET("http://www.pixiv.net/member_illust.php");
            http.setNameValuePair("mode", "medium");
            http.setNameValuePair("illust_id", id);
            uA.send(http);
            Vector<String> meta = getMeta();
            
             
            //sets the filePath to artists folder
            File f  = new File(filePath + meta.get(1));
            if (!f.exists())
            	f.mkdir();
            filePath += meta.get(1) + "/";
            
            int cutIndex = filePath.length() - (meta.get(1).length() + 1); //prep for cutting artist folder from filePath
            
            
            if(haveAlready(meta.get(2)))
            {
            	filePath = filePath.substring(0, cutIndex);
                return;
            }
            
            
            el = uA.doc.findFirst("img class=\"original-image\"");
            f = getImage(el.getAtString("data-src"), sanitize(meta.get(0)));
            
            try
            {
            changeExifMetadata(f, meta);
            setHave(meta.get(2));
            }
            catch(IOException e)
            {
            	
            }
            catch(ImageReadException e)
            {
            	
            }
            catch(ImageWriteException e)
            {
            	
            }
            uA.visit(backURL);
            filePath = filePath.substring(0, cutIndex);
            
    }
    public void getMulti(String unique)throws JauntException
	{
    	Element el;
    	String id = urlToID(unique);
        String backURL = uA.doc.getUrl();
        HttpRequest http = HttpRequest.makeGET("http://www.pixiv.net/member_illust.php");
        http.setNameValuePair("mode", "medium");
        http.setNameValuePair("illust_id", id);
        uA.send(http);
	    Vector<String> meta = getMeta();
	    
	    
	    //sets the filePath to artists folder
	    File f  = new File(filePath + meta.get(1));
	    if (!f.exists())
	    	f.mkdir();
	    filePath += meta.get(1) + "/";
	    
	    int cutIndex = filePath.length() - (meta.get(1).length() + 1); //prep for cutting artist folder from filePath
	    
	    
	    if(haveAlready(meta.get(2)))
	    {
	    	filePath = filePath.substring(0, cutIndex);
	    	return;
	    }
	        
	    
	    String folderName = sanitize(meta.get(0));
	    folderName = createFolder(folderName);
	    
	    HttpRequest getManga = HttpRequest.makeGET("http://www.pixiv.net/member_illust.php");
        getManga.setNameValuePair("mode", "manga");
        getManga.setNameValuePair("illust_id", id);
	    uA.send(getManga);
	    
	    el = uA.doc.findFirst("section class=\"manga\"");
	    Elements els = el.findEvery("img data-filter=\"manga-image\"");
	    Iterator<Element> itr = els.iterator();
	    String src;
	    for(int i = 1; i < els.size()+1; i++)
	    {
	        el = itr.next();
	        src = el.getAtString("data-src");
	        f = getImage(src, folderName + "/" + i);
	        try
	        {
	            changeExifMetadata(f, meta);
	        }
	        catch(IOException e)
	        {
	        	
	        }
	        catch(ImageReadException e)
	        {
	        	
	        }
	        catch(ImageWriteException e)
	        {
	        	
	        }
	    }
	    try
	    {
	    	setHave(meta.get(2));
	    }
	    catch(IOException e)
	    {
	    	System.err.println(e);
	    }
	    uA.visit(backURL);
	    filePath = filePath.substring(0, cutIndex);
	}
	public void getPage(String URL)throws JauntException
	{
	    String backURL = uA.getLocation();
	    uA.visit(URL);
	    Elements els = uA.doc.findEvery("li class=\"image-item\"");
	    
	    Iterator<Element> itr = els.iterator();
	    int count=1;
	    Element el;
	    
	    
	    while(itr.hasNext())
	    {
	        el = itr.next();
	        System.out.println("Fetching picture "+ count + " of " + els.size() + ".");
	        count++;
	        el = el.findFirst("a");
	        String nextPage = el.getAtString("href");
	        if(el.getAtString("class").equals("work  _work multiple "))
	            getMulti(nextPage);
	        else if(el.getAtString("class").equals("work  _work "))
	            getSingle(nextPage);
	    }
	    uA.visit(backURL);
	}
	public void getArtist(String unique)throws JauntException
     {
        String backURL = uA.doc.getUrl();
        String id = urlToID(unique);
        HttpRequest http = HttpRequest.makeGET("http://www.pixiv.net/member_illust.php");
        http.setNameValuePair("id",id);
        http.setNameValuePair("type","all");
        http.setNameValuePair("p", "1");
        uA.send(http);
        Element el;
        
        
        
        try{
         el = uA.doc.findFirst("ul class=\"page-list\"");
        }
        catch(NotFound e)
        {
        	System.out.println("This is page 1 of 1");
            getPage(uA.getLocation());
            uA.visit(backURL);
            return;
        }
        Elements els = el.findEvery("a");
        int size = els.size()+1;
        
        for(int i = 1; i <= size; i++)
        {
            System.out.println("This is page "+ i + " of " + size + ".");
            http.setNameValuePair("p", ""+i);
            uA.send(http);
            getPage(uA.getLocation());
        }
        uA.visit(backURL);
    }
    private void login()
	{
	    try{
	        uA.visit("http://www.pixiv.net/");
	        Element el;
	        el = uA.doc.findFirst("body");
	        if(!el.getAtString("class").equals("not-logged-in"))
	            return;
	        el = uA.doc.findFirst("a class=\"signup-form__submit--login\"");
	        String nextURL = el.getAt("href");
	        uA.visit(nextURL);
	        System.out.println("Login Page Reached");
	        System.out.println(uA.getLocation());
	        
	
	        Form login = uA.doc.getForm(0);
	        login.set("pixiv_id", userName);
	        login.set("password", password);
	        login.submit();
	        System.out.println("Login Complete");
	        System.out.println(uA.getLocation());
	        
	    }
	    catch(JauntException e)
	    {
	         System.err.println(e);
	    }
	}
	private String sanitize(String input)
    {
        Pattern p = Pattern.compile("[\\/\\\\\\:\\*\\?\\\"\\<\\>\\|]");
        Matcher m = p.matcher(input);
        input = m.replaceAll("~~~");
        return input;
    }
	private String urlToID(String url) 
	{
		int subStart = url.indexOf("id=");
		if(subStart == -1)
			return url;
		url = url.substring(subStart + 3);
		return url.split("&")[0];
	}
    private Vector<String> getMeta()throws JauntException
    {
        Element el;
        Elements els;
        Iterator<Element> itr;
        
        //adds title [0]
        el = uA.doc.findFirst("div class=\"user-reaction\"");
        el = el.nextSiblingElement().nextSiblingElement();
        Vector<String> output = new Vector<String>(20);
        output.add(el.innerHTML());
        
        //adds artist [1]
        el = uA.doc.findFirst("h1 class=\"user\"");
        output.add(el.innerHTML());
        
        //adds url [2]
        output.add(uA.getLocation());
        
        //adds tags [3-end]
        els = uA.doc.findEvery("li class=\"tag\"");
        itr = els.iterator();
        while(itr.hasNext())
        {
            el = itr.next();
            el = el.getFirst("a class=\"text\"");
            output.add(el.innerHTML());
        }
        
        return output;
    }
    private void changeExifMetadata(final File jpegImageFile, Vector<String> v)
	        throws IOException, ImageReadException, ImageWriteException {
	    File temp = new File("temp0123456789.jpg");
	    try (FileOutputStream fos = new FileOutputStream(temp);
	            OutputStream os = new BufferedOutputStream(fos);) {
	        
	        TiffOutputSet outputSet = null;
	
	        // note that metadata might be null if no metadata is found.
	        final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
	        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
	        if (null != jpegMetadata) {
	            // note that exif might be null if no Exif metadata is found.
	            final TiffImageMetadata exif = jpegMetadata.getExif();
	
	            if (null != exif) {
	                // TiffImageMetadata class is immutable (read-only).
	                // TiffOutputSet class represents the Exif data to write.
	                //
	                // Usually, we want to update existing Exif metadata by
	                // changing
	                // the values of a few fields, or adding a field.
	                // In these cases, it is easiest to use getOutputSet() to
	                // start with a "copy" of the fields read from the image.
	                outputSet = exif.getOutputSet();
	            }
	        }
	
	        // if file does not contain any exif metadata, we create an empty
	        // set of exif metadata. Otherwise, we keep all of the other
	        // existing tags.
	        if (null == outputSet) {
	            outputSet = new TiffOutputSet();
	        }
	
	        {
	            // Example of how to add a field/tag to the output set.
	            //
	            // Note that you should first remove the field/tag if it already
	            // exists in this directory, or you may end up with duplicate
	            // tags. See above.
	            //
	            // Certain fields/tags are expected in certain Exif directories;
	            // Others can occur in more than one directory (and often have a
	            // different meaning in different directories).
	            //
	            // TagInfo constants often contain a description of what
	            // directories are associated with a given tag.
	            //
	            final TiffOutputDirectory exifDirectory = outputSet.getOrCreateRootDirectory();
	            // make sure to remove old value if present (this method will
	            // not fail if the tag does not exist).
	            
	            exifDirectory.removeField(MicrosoftTagConstants.EXIF_TAG_XPTITLE);
	            exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPTITLE, v.get(0));
	            exifDirectory.removeField(MicrosoftTagConstants.EXIF_TAG_XPAUTHOR);
	            exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPAUTHOR, v.get(1));
	            exifDirectory.removeField(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT);
	            exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPCOMMENT, v.get(2));
	            exifDirectory.sortFields();
	            String s = "";
	            if((v.size() - 3) > 0)
	            {
	            	s = v.get(3);
	            }
	            for(int i = 4; i < v.size();i++)
	            {
	            	s += ";";
	            	s += v.get(i);
	            
	            }
	            exifDirectory.removeField(MicrosoftTagConstants.EXIF_TAG_XPKEYWORDS);
	            exifDirectory.add(MicrosoftTagConstants.EXIF_TAG_XPKEYWORDS, s);
	            
	        }
	
	
	        new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
	                outputSet);
	        Files.copy(temp.toPath(), jpegImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        temp.delete();
	    }
	}
	private String createFolder(String title)
    {
        int count = 1;
        String name = filePath + title;
        File f  = new File(name);
        
        while(f.exists())
        {
            f = new File(filePath + title + "(" + count + ")");
            count++;
        }
        f.mkdir();
        return f.getName();

        
    }
    private File createFile(String title, String filetype)
    {
        int count = 1;
        String name = filePath + title;
        File f = new File(name + "." + filetype);
        
        while(f.exists())
        {
            f = new File(name + "(" + count + ")." + filetype);
            count++;
        }
        return f;
    }
    @SuppressWarnings("unchecked")
	private boolean haveAlready(String unique)
    {
    	File f = new File(filePath + "hash.xml");
    	boolean output = false;
    	HashMap<String, String> hash = null;
    	
    	if(!f.exists())
    		return false;
    	try
    	{
    		FileInputStream is = new FileInputStream(f);
    		XMLDecoder xmlDec = new XMLDecoder(is);
            hash = (HashMap<String,String>)xmlDec.readObject();
            xmlDec.close();
            output = hash.containsKey(unique);
    	
    	}
    	catch(IOException e)
    	{
    		System.err.println(e);
    	}
    	return output;
    }
    @SuppressWarnings("unchecked")
	private void setHave(String url)throws IOException
    {
    	File f = new File(filePath + "hash.xml");
    	HashMap<String, String> hash;
    	
    	//reads or creates HashMap
    	if(!f.exists())
    	{
    		f.createNewFile();
    		hash = new HashMap<String, String>(10000);
    		
    	}
    	else
    	{
	    	FileInputStream is = new FileInputStream(f);
	        XMLDecoder xmlDec = new XMLDecoder(is);
	        hash = (HashMap<String,String>)xmlDec.readObject();
	        xmlDec.close();
    	}
    	
    	hash.put(url, "");
    	
    	//writes HashMap to XML file
    	FileOutputStream fos = new FileOutputStream(f);
        XMLEncoder xmlEnc = new XMLEncoder(fos);
        xmlEnc.writeObject(hash);
        xmlEnc.close();
    }
    
}
