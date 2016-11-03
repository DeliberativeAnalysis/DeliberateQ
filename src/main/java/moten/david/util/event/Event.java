package moten.david.util.event;

public class Event
{
  private Object object;
  private EventType type;
  
  public Event(Object o)
  {
    this.object = o;
  }
  
  public Event(Object o, EventType type)
  {
    this.object = o;
    this.type = type;
  }
  
  public Object getObject()
  {
    return this.object;
  }
  
  public EventType getType()
  {
    return this.type;
  }
}
