package me.doppey.tjbot.commands.fun.memes.crabrave;

//
//  GifSequenceWriter.java
//
//  Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.doppey.tjbot.InoriChan;
import me.doppey.tjbot.commandsystem.IgnoreCommand;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@IgnoreCommand //TODO remove later maybe?
public class GifSequenceWriter extends Command {

    public GifSequenceWriter() {
        this.name = "crabrave";
    }

    protected ImageWriter gifWriter;
    protected ImageWriteParam imageWriteParam;
    protected IIOMetadata imageMetaData;

    /**
     * Creates a new GifSequenceWriter
     *
     * @param outputStream        the ImageOutputStream to be written to
     * @param imageType           one of the imageTypes specified in BufferedImage
     * @param timeBetweenFramesMS the time between frames in miliseconds
     * @param loopContinuously    wether the gif should loop repeatedly
     * @throws IIOException if no gif ImageWriters are found
     * @author Elliot Kroo (elliot[at]kroo[dot]net)
     */
    public GifSequenceWriter(
            ImageOutputStream outputStream,
            int imageType,
            int timeBetweenFramesMS,
            boolean loopContinuously) throws IOException {
        // my method to create a writer
        gifWriter = getWriter();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(imageType);

        imageMetaData =
                gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                        imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode)
                imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(
                root,
                "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "transparentColorFlag",
                "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "delayTime",
                Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute(
                "transparentColorIndex",
                "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = getNode(
                root,
                "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte)
                ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        imageMetaData.setFromTree(metaFormatName, root);

        gifWriter.setOutput(outputStream);

        gifWriter.prepareWriteSequence(null);
    }

    public void writeToSequence(RenderedImage img) throws IOException {
        gifWriter.writeToSequence(
                new IIOImage(
                        img,
                        null,
                        imageMetaData),
                imageWriteParam);
    }

    /**
     * Close this GifSequenceWriter object. This does not close the underlying
     * stream, just finishes off the GIF.
     */
    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }

    /**
     * Returns the first available GIF ImageWriter using
     * ImageIO.getImageWritersBySuffix("gif").
     *
     * @return a GIF ImageWriter object
     * @throws IIOException if no GIF image writers are returned
     */
    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if
     * the requested node does not exist).
     *
     * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param nodeName the name of the child node.
     * @return the child node, if found or a new node created with the given name.
     */
    private static IIOMetadataNode getNode(
            IIOMetadataNode rootNode,
            String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
                    == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }

    /**
     * public GifSequenceWriter(
     * BufferedOutputStream outputStream,
     * int imageType,
     * int timeBetweenFramesMS,
     * boolean loopContinuously) {
     */

//    public  {
//
//
//
//
//
//            // grab the output image type from the first image in the sequence
//            BufferedImage firstImage = ImageIO.read(new File("C:\\Users\\rolli\\IdeaProjects\\tjbot\\src\\main\\java\\EventsAndCommands\\FunCommands\\MemeCommands\\CrabRaveMeme\\CrabRaveSingleImages\\rave.gif"));
//
//            // create a new BufferedOutputStream with the last argument
//            ImageOutputStream output =
//                    new FileImageOutputStream(new File("C:\\Users\\rolli\\IdeaProjects\\tjbot\\src\\main\\java\\EventsAndCommands\\FunCommands\\MemeCommands\\CrabRaveMeme\\CrabRaveSingleImages\\done.gif"));
//
//            // create a gif sequence with the type of the first image, 1 second
//            // between frames, which loops continuously
//            GifSequenceWriter writer =
//                    new GifSequenceWriter(output, firstImage.getType(), 67, true);
//
//            // write out the first image to our sequence...
//            writer.writeToSequence(firstImage);
//            for(int i=1; i < 52; i++) {
//
//
//                    BufferedImage nextImage = ImageIO.read(new File("C:\\Users\\rolli\\IdeaProjects\\tjbot\\src\\main\\java\\EventsAndCommands\\FunCommands\\MemeCommands\\CrabRaveMeme\\CrabRaveSingleImages\\"+i+".gif"));
//                    Graphics graphics = nextImage.getGraphics();
//                    graphics.setColor(Color.BLACK);
//                    graphics.setFont(new Font("Arial Black", Font.BOLD, 35));
//                    graphics.drawString("My code works",100,80);
//                    writer.writeToSequence(nextImage);
//                    System.out.println("Wrote "+i);
//
//            }
//
//            writer.close();
//            output.close();
//
//    }
    @Override
    protected void execute(CommandEvent commandEvent) {
        String text = commandEvent.getArgs();
        // grab the output image type from the first image in the sequence
        BufferedImage firstImage = null;
        try {
            firstImage = ImageIO.read(new File("./crabrave/beginning.gif"));
        } catch (IOException e) {
            InoriChan.LOGGER.error("Couldn't read beginning file for gif", e);
        }

        // create a new BufferedOutputStream with the last argument
        ImageOutputStream output =
                null;
        try {
            output = new FileImageOutputStream(new File("./crabrave/done.gif"));
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
        }

        // create a gif sequence with the type of the first image, 1 second
        // between frames, which loops continuously
        GifSequenceWriter writer =
                null;
        try {
            writer = new GifSequenceWriter(output, firstImage.getType(), 67, true);
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
            ;
        }

        // write out the first image to our sequence...
        try {
            writer.writeToSequence(firstImage);
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
        }

        for (int i = 0; i < 51; i++) {

            try {
                BufferedImage nextImage = null;
                try {
                    nextImage = ImageIO.read(new File("/crabrave/" + i + ".png"));
                } catch (IOException e) {
                    InoriChan.LOGGER.error(e.getMessage(), e);
                }

                Graphics graphics = null;
                if (nextImage != null) {
                    graphics = nextImage.getGraphics();
                }
                if (graphics != null) {
                    graphics.setColor(Color.BLACK);
                }
                if (graphics != null) {
                    graphics.setFont(new Font("Arial Black", Font.BOLD, 35));
                }
                if (graphics != null) {
                    graphics.drawString(text, 100, 80);
                }
                try {
                    writer.writeToSequence(nextImage);
                } catch (IOException e) {
                    InoriChan.LOGGER.error(e.getMessage(), e);
                }

                InoriChan.LOGGER.info("Wrote {}", i);
            } catch (Exception e) {
                InoriChan.LOGGER.error("couldn't write to {}", i, e);
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
        }
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            InoriChan.LOGGER.error(e.getMessage(), e);
        }

        commandEvent.getChannel().sendFile(new File("/crabrave/done.gif")).queue();
    }
}