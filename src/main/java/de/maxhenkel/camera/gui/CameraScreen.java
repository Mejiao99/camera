package de.maxhenkel.camera.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.camera.ClientImageUploadManager;
import de.maxhenkel.camera.ImageTools;
import de.maxhenkel.camera.Main;
import de.maxhenkel.camera.Shaders;
import de.maxhenkel.camera.net.MessageRequestUploadCustomImage;
import de.maxhenkel.camera.net.MessageSetShader;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

public class CameraScreen extends ScreenBase<Container> {

    private static final ResourceLocation CAMERA_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/camera.png");
    private static final int FONT_COLOR = 4210752;
    private static final int PADDING = 10;
    private static final int BUTTON_WIDTH = 70;
    private static final int BUTTON_HEIGHT = 20;

    private int index = 0;

    private Button upload;

    public CameraScreen(String currentShader) {
        super(CAMERA_TEXTURE, new DummyContainer(), null, new TranslationTextComponent("gui.camera.title"));
        imageWidth = 248;
        imageHeight = 109;

        for (int i = 0; i < Shaders.SHADER_LIST.size(); i++) {
            String s = Shaders.SHADER_LIST.get(i);
            if (currentShader == null) {
                if (s.equals("none")) {
                    index = i;
                    break;
                }
            } else if (s.equals(currentShader)) {
                index = i;
                break;
            }
        }
    }

    // https://github.com/MinecraftForge/MinecraftForge/commit/007cd42ec6eed0e023c1324525cd44484ee0e79c
    @Override
    protected void init() {
        super.init();
        buttons.clear();
        addButton(new Button(leftPos + PADDING, topPos + PADDING + font.lineHeight + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslationTextComponent("button.camera.prev"), button -> {
            index--;
            if (index < 0) {
                index = Shaders.SHADER_LIST.size() - 1;
            }
            sendShader();
        }));
        addButton(new Button(leftPos + imageWidth - BUTTON_WIDTH - PADDING, topPos + PADDING + font.lineHeight + PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslationTextComponent("button.camera.next"), button -> {
            index++;
            if (index >= Shaders.SHADER_LIST.size()) {
                index = 0;
            }
            sendShader();
        }));

        if (Main.SERVER_CONFIG.allowImageUpload.get()) {
            upload = addButton(new Button(leftPos + imageWidth / 2 - BUTTON_WIDTH / 2, topPos + imageHeight - BUTTON_HEIGHT - PADDING, BUTTON_WIDTH, BUTTON_HEIGHT, new TranslationTextComponent("button.camera.upload"), button -> {
                ImageTools.chooseImage(file -> {
                    try {
                        UUID uuid = UUID.randomUUID();
                        BufferedImage image = ImageTools.loadImage(file);
                        ClientImageUploadManager.addImage(uuid, image);
                        Main.SIMPLE_CHANNEL.sendToServer(new MessageRequestUploadCustomImage(uuid));
                    } catch (IOException e) {
                        minecraft.player.displayClientMessage(new TranslationTextComponent("message.upload_error", e.getMessage()), true);
                        e.printStackTrace();
                    }
                    minecraft.screen = null;
                });
            }));
        }
    }

    @Override
    public void tick() {
        super.tick();
        upload.active = !ImageTools.isFileChooserOpen();
    }

    private void sendShader() {
        Main.SIMPLE_CHANNEL.sendToServer(new MessageSetShader(Shaders.SHADER_LIST.get(index)));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);

        TranslationTextComponent chooseFilter = new TranslationTextComponent("gui.camera.choose_filter");
        int chooseFilterWidth = font.width(chooseFilter);
        font.draw(matrixStack, chooseFilter.getVisualOrderText(), imageWidth / 2 - chooseFilterWidth / 2, 10, FONT_COLOR);

        TranslationTextComponent shaderName = new TranslationTextComponent("shader." + Shaders.SHADER_LIST.get(index));
        int shaderWidth = font.width(shaderName);
        font.draw(matrixStack, shaderName.getVisualOrderText(), imageWidth / 2 - shaderWidth / 2, PADDING + font.lineHeight + PADDING + BUTTON_HEIGHT / 2 - font.lineHeight / 2, TextFormatting.WHITE.getColor());

        TranslationTextComponent uploadImage = new TranslationTextComponent("gui.camera.upload_image");
        int uploadImageWidth = font.width(uploadImage);
        font.draw(matrixStack, uploadImage.getVisualOrderText(), imageWidth / 2 - uploadImageWidth / 2, imageHeight - PADDING - BUTTON_HEIGHT - PADDING - font.lineHeight, FONT_COLOR);
    }

}