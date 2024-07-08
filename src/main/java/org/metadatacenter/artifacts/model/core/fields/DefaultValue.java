
package org.metadatacenter.artifacts.model.core.fields;

import com.fasterxml.jackson.annotation.JsonIgnore;

public sealed interface DefaultValue<T> permits TextDefaultValue, NumericDefaultValue, ControlledTermDefaultValue,
  TemporalDefaultValue, LinkDefaultValue, EmailDefaultValue, PhoneNumberDefaultValue
{
   @JsonIgnore
   DefaultValueType getValueType();

   @JsonIgnore
   T value();

   @JsonIgnore
   default boolean isTextDefaultValue() { return getValueType() == DefaultValueType.TEXT; }

   @JsonIgnore
   default boolean isNumericDefaultValue() { return getValueType() == DefaultValueType.NUMERIC; }

   @JsonIgnore
   default boolean isTemporalDefaultValue() { return getValueType() == DefaultValueType.TEMPORAL; }

   @JsonIgnore
   default boolean isLinkDefaultValue() { return getValueType() == DefaultValueType.LINK; }

   @JsonIgnore
   default boolean isControlledTermDefaultValue() { return getValueType() == DefaultValueType.CONTROLLED_TERM; }

   default TextDefaultValue asTextDefaultValue()
   {
      if (getValueType() == DefaultValueType.TEXT)
         return (TextDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TextDefaultValue.class.getName());
   }

   default NumericDefaultValue asNumericDefaultValue()
   {
      if (getValueType() == DefaultValueType.NUMERIC)
         return (NumericDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericDefaultValue.class.getName());
   }

   default LinkDefaultValue asLinkDefaultValue()
   {
      if (getValueType() == DefaultValueType.LINK)
         return (LinkDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + LinkDefaultValue.class.getName());
   }

   default ControlledTermDefaultValue asControlledTermDefaultValue()
   {
      if (getValueType() == DefaultValueType.CONTROLLED_TERM)
         return (ControlledTermDefaultValue)this;
      else
         return null;
   }

   default TemporalDefaultValue asTemporalDefaultValue()
   {
      if (getValueType() == DefaultValueType.TEMPORAL)
         return (TemporalDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + TemporalDefaultValue.class.getName());
   }

}
