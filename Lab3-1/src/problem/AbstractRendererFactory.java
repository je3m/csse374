package problem;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRendererFactory implements IRendererFactory {
	private Map<Class<? extends IComponent>, Class<? extends IRenderer>> componentToRender;

	public AbstractRendererFactory() {
		this.componentToRender = new HashMap<Class<? extends IComponent>, Class<? extends IRenderer>>();
		this.populate();
	}

	@Override
	public IRenderer createRenderer(IComponent c) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Class<? extends IComponent> cClass =  c.getClass();
		Class<? extends IRenderer> rClass = this.componentToRender.get(cClass);

		return rClass.getConstructor(IComponent.class).newInstance(c);
	}

	protected Class<? extends IRenderer> get(Class<? extends IComponent> cClass) {
		return this.componentToRender.get(cClass);
	}

	protected abstract void populate();

	protected void put(Class<? extends IComponent> cClass, Class<? extends IRenderer> rClass) {
		this.componentToRender.put(cClass, rClass);
	}
}
