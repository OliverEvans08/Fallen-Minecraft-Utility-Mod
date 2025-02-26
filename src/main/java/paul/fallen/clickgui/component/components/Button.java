package paul.fallen.clickgui.component.components;

import net.minecraft.client.Minecraft;
import paul.fallen.FALLENClient;
import paul.fallen.clickgui.component.Component;
import paul.fallen.clickgui.component.Frame;
import paul.fallen.clickgui.component.components.sub.Checkbox;
import paul.fallen.clickgui.component.components.sub.*;
import paul.fallen.clickgui.settings.Setting;
import paul.fallen.module.Module;
import paul.fallen.utils.render.UIUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 *  Made by Pandus1337
 *  it's free to use,
 *  but you have to credit me
 *  @author Pandus1337
 *  <a href="https://github.com/Pandus1337/ClickGUI">...</a>
 */

public class Button extends Component {

	public Module mod;
	public Frame parent;
	public int offset;
	private boolean isHovered;
	private final ArrayList<Component> subcomponents;
	public boolean open;
	private final int height;
	
	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if(FALLENClient.INSTANCE.getSettingManager().getSettingsByMod(mod) != null) {
			for(Setting s : FALLENClient.INSTANCE.getSettingManager().getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
				if (s.isColorSlider()) {
					this.subcomponents.add(new ColorSlider(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	@Override
	public void renderComponent() {
		UIUtils.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getWidth(), 12, this.isHovered ? (this.mod.toggled ? new Color(255, 0, 255, 191).darker().getRGB() : new Color(15, 15, 15, 191).getRGB()) : (this.mod.toggled ? new Color(255, 0, 255, 191).getRGB() : new Color(30, 30, 30, 191).getRGB()));
		//Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isToggled() ? new Color(255, 0, 255, 191).darker().getRGB() : new Color(15, 15, 15, 191).getRGB()) : (this.mod.isToggled() ? new Color(255, 0, 255, 191).getRGB() : new Color(30, 30, 30, 191).getRGB()));
		UIUtils.drawTextOnScreen(this.mod.getName(), (parent.getX() + 2) + 2, (parent.getY() + offset + 2) + 1, new Color(255, 255, 255).getRGB());
		//Fonts.ARIAL.ARIAL_18.ARIAL_18.drawString(this.mod.getName(), (parent.getX() + 2) + 2, (parent.getY() + offset + 2) + 1, new Color(255, 255, 255).getRGB(), true);
		//if(this.subcomponents.size() > 2)
		if (this.subcomponents.size() > 0)
			UIUtils.drawTextOnScreen(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10), (parent.getY() + offset) + 4, new Color(255, 255, 255, 255).getRGB());
		//Fonts.ARIAL.ARIAL_18.ARIAL_18.drawString(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10), (parent.getY() + offset) + 4, new Color(255, 255, 255, 255).getRGB(), true);
		if (this.open) {
			if (!this.subcomponents.isEmpty()) {
				for (Component comp : this.subcomponents) {
					comp.renderComponent();
				}
				UIUtils.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, 3, ((this.subcomponents.size() + 1) * 12), new Color(255, 0, 255, 191).getRGB());
				//Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), new Color(255, 0, 255, 191).getRGB());
			}
		}

		if (this.isHovered) {
			UIUtils.drawRect(400, 400 - Minecraft.getInstance().fontRenderer.FONT_HEIGHT, Minecraft.getInstance().fontRenderer.getStringWidth(mod.getDisplayName()), Minecraft.getInstance().fontRenderer.FONT_HEIGHT, new Color(30, 30, 30, 191).getRGB());
			UIUtils.drawTextOnScreenWithShadow(mod.getDisplayName(), 400, 400 - Minecraft.getInstance().fontRenderer.FONT_HEIGHT, Color.WHITE.getRGB());

			UIUtils.drawRect(400, 400, Minecraft.getInstance().fontRenderer.getStringWidth(mod.getDescription()), Minecraft.getInstance().fontRenderer.FONT_HEIGHT, new Color(30, 30, 30, 191).getRGB());
			UIUtils.drawTextOnScreenWithShadow(mod.getDescription(), 400, 400, Color.WHITE.getRGB());
		}
	}


	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.toggle();
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		for(Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		super.keyPressed(keyCode, scanCode, modifiers);
		for(Component comp : this.subcomponents) {
			comp.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
	}

}
