package ru.merkii.rduels.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.config.CustomKitConfiguration;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.gui.click.*;
import ru.merkii.rduels.gui.extractor.*;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractorRegistry;
import ru.merkii.rduels.gui.internal.paged.PageResolverRegistry;
import ru.merkii.rduels.gui.invui.BaseInventoryFactory;
import ru.merkii.rduels.gui.invui.click.RootClickHandlerFactory;
import ru.merkii.rduels.gui.page.*;

@Factory
public class GUIFactory {

    @Bean
    public InventoryGUIFactory guiFactory(MenuConfiguration config, PageResolverRegistry pageResolverRegistry,
                                          ValueExtractorRegistry valueExtractorRegistry) {
        return new BaseInventoryFactory(config, valueExtractorRegistry, pageResolverRegistry);
    }

    @Bean
    public SelectKitClickHandler selectKitClickHandler(MenuConfiguration config) {
        return new SelectKitClickHandler(config);
    }

    @Bean
    public OpenCategoryClickHandler openCategoryClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        return new OpenCategoryClickHandler(config, factory);
    }

    @Bean
    public EditEnchantOrAmountClickHandler editEnchantOrAmountClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        return new EditEnchantOrAmountClickHandler(config, factory);
    }

    @Bean
    public RemoveItemClickHandler removeItemClickHandler(MenuConfiguration config) {
        return new RemoveItemClickHandler(config);
    }

    @Bean
    public SetAmountClickHandler setAmountClickHandler(MenuConfiguration config) {
        return new SetAmountClickHandler(config);
    }

    @Bean
    public ApplyEnchantClickHandler applyEnchantClickHandler(MenuConfiguration config) {
        return new ApplyEnchantClickHandler(config);
    }

    @Bean
    public SelectCustomKitClickHandler selectCustomKitClickHandler() {
        return new SelectCustomKitClickHandler();
    }

    @Bean
    public SelectServerKitClickHandler selectServerKitClickHandler() {
        return new SelectServerKitClickHandler();
    }

    @Bean
    public SelectOptionClickHandler selectOptionClickHandler(MenuConfiguration config) {
        return new SelectOptionClickHandler(config);
    }

    @Bean
    public ChallengePartyClickHandler challengePartyClickHandler(MenuConfiguration config) {
        return new ChallengePartyClickHandler(config);
    }

    @Bean
    public ExitMenuClickHandler exitMenuClickHandler(MenuConfiguration config) {
        return new ExitMenuClickHandler(config);
    }

    @Bean
    public SelectCategoryClickHandler selectCategoryClickHandler(CustomKitStorage customKitStorage, InventoryGUIFactory inventoryGUIFactory, MenuConfiguration configuration) {
        return new SelectCategoryClickHandler(customKitStorage, inventoryGUIFactory, configuration);
    }

    @Bean
    public ChoiceItemClickHandler choiceItemClickHandler(InventoryGUIFactory inventoryGUIFactory) {
        return new ChoiceItemClickHandler(inventoryGUIFactory);
    }

    @Bean
    public DuelRequestArenaClickHandler duelRequestArenaClickHandler(InventoryGUIFactory inventoryGUIFactory) {
        return new DuelRequestArenaClickHandler(inventoryGUIFactory);
    }

    @Bean
    public DuelRequestNumGamesClickHandler duelRequestNumGamesClickHandler(InventoryGUIFactory inventoryGUIFactory) {
        return new DuelRequestNumGamesClickHandler(inventoryGUIFactory);
    }

    @Bean
    public DuelRequestKitClickHandler duelRequestKitClickHandler(InventoryGUIFactory inventoryGUIFactory) {
        return new DuelRequestKitClickHandler(inventoryGUIFactory);
    }

    @Bean
    public RequestFightClickHandler requestFightClickHandler(CustomKitAPI customKitAPI, DuelAPI duelAPI, ArenaAPI arenaAPI) {
        return new RequestFightClickHandler(customKitAPI, duelAPI, arenaAPI);
    }

    @Bean
    public ClickHandlerRegistry clickHandlerRegistry(SelectKitClickHandler selectKitClickHandler,
                                                     OpenCategoryClickHandler openCategoryClickHandler,
                                                     EditEnchantOrAmountClickHandler editEnchantOrAmountClickHandler,
                                                     RemoveItemClickHandler removeItemClickHandler,
                                                     SetAmountClickHandler setAmountClickHandler,
                                                     ApplyEnchantClickHandler applyEnchantClickHandler,
                                                     SelectCustomKitClickHandler selectCustomKitClickHandler,
                                                     SelectServerKitClickHandler selectServerKitClickHandler,
                                                     SelectOptionClickHandler selectOptionClickHandler,
                                                     ChallengePartyClickHandler challengePartyClickHandler,
                                                     ExitMenuClickHandler exitMenuClickHandler,
                                                     SelectCategoryClickHandler selectCategoryClickHandler,
                                                     ChoiceItemClickHandler choiceItemClickHandler,
                                                     DuelRequestKitClickHandler duelRequestKitClickHandler,
                                                     DuelRequestNumGamesClickHandler duelRequestNumGamesClickHandler,
                                                     DuelRequestArenaClickHandler duelRequestArenaClickHandler,
                                                     RequestFightClickHandler requestFightClickHandler) {

        ClickHandlerRegistry clickHandlerRegistry = new ClickHandlerRegistry();

        clickHandlerRegistry.register("root", new RootClickHandlerFactory());
        clickHandlerRegistry.register(RequestFightClickHandler.NAME, requestFightClickHandler);
        clickHandlerRegistry.register(DuelRequestKitClickHandler.NAME, duelRequestKitClickHandler);
        clickHandlerRegistry.register(DuelRequestArenaClickHandler.NAME, duelRequestArenaClickHandler);
        clickHandlerRegistry.register(DuelRequestNumGamesClickHandler.NAME, duelRequestNumGamesClickHandler);
        clickHandlerRegistry.register(SelectKitClickHandler.NAME, selectKitClickHandler);
        clickHandlerRegistry.register(OpenCategoryClickHandler.NAME, openCategoryClickHandler);
        clickHandlerRegistry.register(EditEnchantOrAmountClickHandler.NAME, editEnchantOrAmountClickHandler);
        clickHandlerRegistry.register(RemoveItemClickHandler.NAME, removeItemClickHandler);
        clickHandlerRegistry.register(SetAmountClickHandler.NAME, setAmountClickHandler);
        clickHandlerRegistry.register(ApplyEnchantClickHandler.NAME, applyEnchantClickHandler);
        clickHandlerRegistry.register(SelectCustomKitClickHandler.NAME, selectCustomKitClickHandler);
        clickHandlerRegistry.register(SelectServerKitClickHandler.NAME, selectServerKitClickHandler);
        clickHandlerRegistry.register(SelectOptionClickHandler.NAME, selectOptionClickHandler);
        clickHandlerRegistry.register(ChallengePartyClickHandler.NAME, challengePartyClickHandler);
        clickHandlerRegistry.register(ExitMenuClickHandler.NAME, exitMenuClickHandler);
        clickHandlerRegistry.register(SelectCategoryClickHandler.NAME, selectCategoryClickHandler);
        clickHandlerRegistry.register(ChoiceItemClickHandler.NAME, choiceItemClickHandler);

        return clickHandlerRegistry;
    }

    @Bean
    public PageResolverRegistry pageResolverRegistry(PartyAPI partyAPI, DuelConfiguration duelConfiguration, CustomKitConfiguration customKitConfiguration, MenuConfiguration config, CustomKitStorage customKitStorage) {
        PageResolverRegistry pageResolverRegistry = new PageResolverRegistry();

        pageResolverRegistry.register("CATEGORIES", new CategoriesPageResolver(config.settings().createSettings()));
        pageResolverRegistry.register("KIT_LIST", new KitListPageResolver(config.settings().createSettings()));
        pageResolverRegistry.register("KIT_SLOTS", new KitSlotsPageResolver(customKitStorage));
        pageResolverRegistry.register("ENCHANT_CATEGORIES", new EnchantCategoriesPageResolver(config));
        pageResolverRegistry.register("AMOUNT_OPTIONS", new AmountOptionsPageResolver(customKitConfiguration));
        pageResolverRegistry.register("REQUEST_OPTIONS", new RequestOptionsPageResolver(duelConfiguration.choiceKitMenu()));
        pageResolverRegistry.register("PARTY_LIST", new PartyListPageResolver(partyAPI));
        pageResolverRegistry.register("CHOICE_ITEM_CATEGORY", new ChoiceItemCategoryPageResolver());

        return pageResolverRegistry;
    }

    @Bean
    public ValueExtractorRegistry valueExtractorRegistry(CustomKitAPI customKitAPI, MenuConfiguration config, PartyAPI partyAPI) {
        ValueExtractorRegistry valueExtractorRegistry = new ValueExtractorRegistry();

        valueExtractorRegistry.register(new DuelOptionValueExtractor());
        valueExtractorRegistry.register(new ChoiceItemCategoryValueExtractor());
        valueExtractorRegistry.register(new KitSelectedValueExtractor(customKitAPI, config.messages()));
        valueExtractorRegistry.register(new SlotValueExtractor());
        valueExtractorRegistry.register(new PartyValueExtractor(config, partyAPI));
        valueExtractorRegistry.register(new CategoryValueExtractor());

        return valueExtractorRegistry;
    }

}