
package org.metadatacenter.artifacts.model.core;

public sealed interface DefaultValue<T> permits StringDefaultValue, NumericDefaultValue, UriStringPairDefaultValue
{
   DefaultValueType getValueType();

   T value();

   default boolean isStringDefaultValue() { return getValueType() == DefaultValueType.STRING; }

   default boolean isNumericDefaultValue() { return getValueType() == DefaultValueType.NUMERIC; }

   default boolean isUriStringPairDefaultValue() { return getValueType() == DefaultValueType.URI_STRING_PAIR; }

   default StringDefaultValue asStringDefaultValue()
   {
      if (getValueType() == DefaultValueType.STRING)
         return (StringDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + StringDefaultValue.class.getName());
   }

   default NumericDefaultValue asNumericDefaultValue()
   {
      if (getValueType() == DefaultValueType.NUMERIC)
         return (NumericDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + NumericDefaultValue.class.getName());
   }

   default UriStringPairDefaultValue asURIStringPairDefaultValue()
   {
      if (getValueType() == DefaultValueType.URI_STRING_PAIR)
         return (UriStringPairDefaultValue)this;
      else
         throw new ClassCastException("Cannot convert " + this.getClass().getName() + " to " + UriStringPairDefaultValue.class.getName());
   }
}
