/*******************************************************************************
 * Copyright (c) 2013 Attila Kovacs <attila_kovacs[AT]post.harvard.edu>.
 * All rights reserved. 
 * 
 * This file is part of jnum.
 * 
 *     kovacs.util is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     kovacs.util is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with kovacs.util.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Attila Kovacs <attila_kovacs[AT]post.harvard.edu> - initial API and implementation
 ******************************************************************************/
package jnum.util;

import java.util.Hashtable;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class FlagMaster.
 */
public class FlagMaster {
	
	/** The owner type. */
	Class<?> ownerType;
	
	/**
	 * Register.
	 *
	 * @param owner the owner
	 * @param name the name
	 * @param id the id
	 * @param isHardwareFlag the is hardware flag
	 * @return the int
	 */
	public int register(Class<?> owner, String name, byte id, boolean isHardwareFlag) {
		Flag flag = new Flag(this, owner, name, id, isHardwareFlag);
		list.add(flag);
		return flag.value;
	}
	
	/**
	 * To string.
	 *
	 * @param object the object
	 * @return the string
	 */
	public String toString(Flagging object) {
		Class<?> channelType = object.getClass();
	
		String code = "";
		int value = 1;
		for(int i=0; i<64; i++) if(object.isFlagged(value)) code += forValue(channelType, value).id;
			
		if(code.length() == 0) return "-";
		return code;
	}
	
	/**
	 * Parses the.
	 *
	 * @param object the object
	 * @param code the code
	 */
	public void parse(Flagging object, String code) {
		Class<?> ownerType = object.getClass();
		
		object.unflag();
		
		for(byte c : code.getBytes()) {
			Flag flag = forID(ownerType, c);
			if(flag != null) object.flag(flag.value);
			else System.err.println("WARNING! Unknown flag type '" + c + "' for " + ownerType.getSimpleName() + ".");
		}
	}
	
	/**
	 * Gets the flags.
	 *
	 * @param ownerClass the owner class
	 * @return the flags
	 */
	public ClassLookup getFlags(Class<?> ownerClass) {
		if(!registry.containsKey(ownerClass))
			registry.put(ownerClass, new ClassLookup(ownerClass));
		return registry.get(ownerClass);		
	}
		
	
	/**
	 * Gets the hardware flags.
	 *
	 * @return the hardware flags
	 */
	public int getHardwareFlags() { return hardwareFlags; }
	
	/**
	 * Gets the software flags.
	 *
	 * @return the software flags
	 */
	public int getSoftwareFlags() { return softwareFlags; }
	
	
	/**
	 * Contains id.
	 *
	 * @param type the type
	 * @param id the id
	 * @return true, if successful
	 */
	public boolean containsID(Class<?> type, byte id) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) if(registry.get(ownerType).contains(id)) return true;				
		return false;
	}
	
	/**
	 * Contains name.
	 *
	 * @param type the type
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean containsName(Class<?> type, String name) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) if(registry.get(ownerType).contains(name)) return true;				
		return false;
	}
	
	/**
	 * Contains value.
	 *
	 * @param type the type
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean containsValue(Class<?> type, int value) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) if(registry.get(ownerType).contains(value)) return true;				
		return false;
	}
	
	/**
	 * For id.
	 *
	 * @param type the type
	 * @param id the id
	 * @return the flag
	 */
	public Flag forID(Class<?> type, byte id) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) {
				ClassLookup flags = registry.get(ownerType);
				if(flags.contains(id)) 
					return flags.get(id);
			}
		return null;
	}
	
	/**
	 * For name.
	 *
	 * @param type the type
	 * @param name the name
	 * @return the flag
	 */
	public Flag forName(Class<?> type, String name) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) {
				ClassLookup flags = registry.get(ownerType);
				if(flags.contains(name)) 
					return flags.get(name);
			}
		return null;
	}
	
	/**
	 * For value.
	 *
	 * @param type the type
	 * @param value the value
	 * @return the flag
	 */
	public Flag forValue(Class<?> type, int value) {
		for(Class<?> ownerType: registry.keySet())
			if(ownerType.isAssignableFrom(type)) {
				ClassLookup flags = registry.get(ownerType);
				if(flags.contains(value)) 
					return flags.get(value);
			}
		return null;
	}
	
	
	/**
	 * The Class Flag.
	 */
	public class Flag {
		
		/** The id. */
		byte id;
		
		/** The name. */
		String name;
		
		/** The value. */
		int value;
		
		/** The is hardware flag. */
		boolean isHardwareFlag;
		
		/** The owner. */
		Class<?> owner;
		
		/**
		 * Instantiates a new flag.
		 *
		 * @param manager the manager
		 * @param owner the owner
		 * @param name the name
		 * @param id the id
		 * @param isHardwareFlag the is hardware flag
		 */
		private Flag(FlagMaster manager, Class<?> owner, String name, byte id, boolean isHardwareFlag) {
			this.owner = owner;	
			
			if(containsID(owner, id))
				throw new IllegalArgumentException("Flag id '" + id + "' is already reserved for " + owner.getSimpleName() + ".");
			if(containsName(owner, name))
				throw new IllegalArgumentException("Flag name '" + name + "' is already reserved for " + owner.getSimpleName() + ".");
			
			this.id = id;
			this.name = name;
			this.isHardwareFlag = isHardwareFlag;
				
			value = 1<<(nextBit++);
			
			if(isHardwareFlag) hardwareFlags |= value;
			else softwareFlags |= value;
			
			getFlags(owner).register(this);
		}
	}
		
		
	/**
	 * The Class ClassLookup.
	 */
	public class ClassLookup {
		
		/** The channel type. */
		private Class<?> channelType;

		/** The value lookup. */
		private Hashtable<Integer, Flag> valueLookup = new Hashtable<Integer, Flag>();
		
		/** The id lookup. */
		private Hashtable<Byte, Flag> idLookup = new Hashtable<Byte, Flag>();
		
		/** The name lookup. */
		private Hashtable<String, Flag> nameLookup = new Hashtable<String, Flag>();
		
		/**
		 * Register.
		 *
		 * @param flag the flag
		 */
		public void register(Flag flag) {
			valueLookup.put(flag.value, flag);
			idLookup.put(flag.id, flag);
			nameLookup.put(flag.name, flag);		
		}
		
		/**
		 * Instantiates a new class lookup.
		 *
		 * @param ownerType the owner type
		 */
		public ClassLookup(Class<?> ownerType) {
			this.channelType = ownerType;
		}
		
		/**
		 * Contains.
		 *
		 * @param id the id
		 * @return true, if successful
		 */
		public boolean contains(byte id) { return idLookup.containsKey(id); }
		
		/**
		 * Contains.
		 *
		 * @param name the name
		 * @return true, if successful
		 */
		public boolean contains(String name) { return nameLookup.containsKey(name); }
		
		/**
		 * Contains.
		 *
		 * @param value the value
		 * @return true, if successful
		 */
		public boolean contains(int value) { return valueLookup.containsKey(value); }
		
		/**
		 * Gets the.
		 *
		 * @param id the id
		 * @return the flag
		 */
		public Flag get(byte id) { return idLookup.get(id);	}
		
		/**
		 * Gets the.
		 *
		 * @param name the name
		 * @return the flag
		 */
		public Flag get(String name) { return nameLookup.get(name); }
		
		/**
		 * Gets the.
		 *
		 * @param value the value
		 * @return the flag
		 */
		public Flag get(int value) { return valueLookup.get(value); }
		
		
		/**
		 * Gets the channel type.
		 *
		 * @return the channel type
		 */
		public Class<?> getChannelType() { return channelType; }
		
	}

	
	
	/** The list. */
	private Vector<Flag> list = new Vector<Flag>();
	
	/** The registry. */
	private Hashtable<Class<?>, ClassLookup> registry = new Hashtable<Class<?>, ClassLookup>();
	
	/** The hardware flags. */
	private int hardwareFlags = 0;
	
	/** The software flags. */
	private int softwareFlags = 0;
	
	/** The next bit. */
	private int nextBit = 0;
}
